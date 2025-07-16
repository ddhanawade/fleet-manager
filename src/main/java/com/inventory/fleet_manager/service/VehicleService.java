package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.configuration.LoggedInUserHolder;
import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.dto.VehicleOrderResponse;
import com.inventory.fleet_manager.exception.ConstraintViolationException;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.TestDrive;
import com.inventory.fleet_manager.model.User;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.TestDriveRepository;
import com.inventory.fleet_manager.repository.VehicleRepository;
import com.inventory.fleet_manager.utility.VehicleUtils;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.io.InputStreamReader;

@Service
@Slf4j
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    private final TestDriveRepository testDriveRepository;

    private final VehicleMapper vehicleMapper;

    private final UserDetailsServiceImpl userDetailsService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Custom thread pool

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper, TestDriveRepository testDriveRepository, UserDetailsServiceImpl userDetailsService, UserDetailsServiceImpl userDetailsService1) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.testDriveRepository = testDriveRepository;
        this.userDetailsService = userDetailsService1;
    }


    /**
     * Retrieves all vehicles from the repository, processes them asynchronously,
     * and returns a list of VehicleDTOs with calculated ages.
     *
     * @return List of VehicleDTOs
     */

    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (vehicles.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no vehicles found
        }

        List<CompletableFuture<VehicleDTO>> futures = vehicles.stream()
                .map(vehicle -> CompletableFuture.supplyAsync(() -> {
                            VehicleDTO dto = vehicleMapper.toDTO(vehicle); // Ensure vehicleMapper is thread-safe

                            return dto;
                        }, executorService) // Use custom executor
                        .exceptionally(ex -> {
                            // Log the exception and return a fallback DTO
                            System.err.println("Error processing vehicle: " + ex.getMessage());
                            return new VehicleDTO(); // Return a default or fallback DTO
                        }))
                .collect(Collectors.toList());

        // Wait for all tasks to complete and collect results
        return futures.stream()
                .map(CompletableFuture::join) // Wait for each future to complete
                .collect(Collectors.toList());
    }

    public void measurePerformance() {
        // Measure performance without threading
        long startWithoutThreading = System.nanoTime();
        List<VehicleDTO> resultWithoutThreading = getAllVehiclesWithoutThreading(); // Implement a non-threaded version
        long endWithoutThreading = System.nanoTime();
        System.out.println("Execution time without threading: " + (endWithoutThreading - startWithoutThreading) / 1_000_000 + " ms");

        // Measure performance with threading
        long startWithThreading = System.nanoTime();
        List<VehicleDTO> resultWithThreading = getAllVehicles(); // Threaded version
        long endWithThreading = System.nanoTime();
        System.out.println("Execution time with threading: " + (endWithThreading - startWithThreading) / 1_000_000 + " ms");
    }

    public List<VehicleDTO> getAllVehiclesWithoutThreading() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Process each vehicle synchronously
        return vehicles.stream()
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDTO(vehicle); // Ensure vehicleMapper is thread-safe

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(Long id) throws VehicleNotFoundException {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        return vehicleMapper.toDTO(vehicle.get());
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        try {
            if (vehicleDTO.getInvoiceDate() != null ) {
                String invoiceDate = VehicleUtils.convertInvoiceDate(String.valueOf(vehicleDTO.getInvoiceDate()));
                Integer newAge = VehicleUtils.calculateVehicleAge(invoiceDate);
                vehicleDTO.setAge(newAge);

                if (vehicleDTO.getInvoiceValue() != null) {
                    String newInterest = VehicleUtils.calculateInterest(vehicleDTO.getInvoiceValue(), newAge);
                    vehicleDTO.setInterest(newInterest);
                }
            }
            vehicleDTO.setCreatedBy(userDetailsService.getLoggedInUsername());
            vehicleDTO.setLocation(vehicleDTO.getLocation().toUpperCase());
            Vehicle savedVehicle = vehicleRepository.save(vehicleMapper.toEntity(vehicleDTO));
            return vehicleMapper.toDTO(savedVehicle);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("chassisNumber")) {
                throw new ConstraintViolationException("A vehicle with the same chassis number already exists.");
            }
            throw new RuntimeException("An error occurred while saving the vehicle.", ex);
        }
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        if (vehicleDTO == null) {
            throw new IllegalArgumentException("VehicleDTO cannot be null");
        }
        String loggedInUser = userDetailsService.getLoggedInUsername();
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));


        if (vehicleDTO.getInvoiceDate() != null &&
                !vehicleDTO.getInvoiceDate().equals(existingVehicle.getInvoiceDate())) {

            String invoiceDate = VehicleUtils.convertInvoiceDate(String.valueOf(vehicleDTO.getInvoiceDate()));
            Integer newAge = VehicleUtils.calculateVehicleAge(invoiceDate);
            vehicleDTO.setAge(newAge);

            if (vehicleDTO.getInvoiceValue() != null) {
                String newInterest = VehicleUtils.calculateInterest(vehicleDTO.getInvoiceValue(), newAge);
                vehicleDTO.setInterest(newInterest);
            }
        }
        if (vehicleDTO.getMake() != null && !vehicleDTO.getMake().equals(existingVehicle.getMake())) {
            existingVehicle.setMake(vehicleDTO.getMake());
        }
        if (vehicleDTO.getModel() != null && !vehicleDTO.getModel().equals(existingVehicle.getModel())) {
            existingVehicle.setModel(vehicleDTO.getModel().toUpperCase());
        }
        if (vehicleDTO.getGrade() != null && !vehicleDTO.getGrade().equals(existingVehicle.getGrade())) {
            existingVehicle.setGrade(vehicleDTO.getGrade());
        }
        if (vehicleDTO.getFuelType() != null && !vehicleDTO.getFuelType().equals(existingVehicle.getFuelType())) {
            existingVehicle.setFuelType(vehicleDTO.getFuelType());
        }
        if (vehicleDTO.getExteriorColor() != null && !vehicleDTO.getExteriorColor().equals(existingVehicle.getExteriorColor())) {
            existingVehicle.setExteriorColor(vehicleDTO.getExteriorColor());
        }
        if (vehicleDTO.getInteriorColor() != null && !vehicleDTO.getInteriorColor().equals(existingVehicle.getInteriorColor())) {
            existingVehicle.setInteriorColor(vehicleDTO.getInteriorColor());
        }
        if (vehicleDTO.getLocation() != null && !vehicleDTO.getLocation().equals(existingVehicle.getLocation())) {
            existingVehicle.setLocation(vehicleDTO.getLocation().toUpperCase());
        }
        if (vehicleDTO.getVehicleStatus() != null) {
            try {
                existingVehicle.setVehicleStatus(vehicleDTO.getVehicleStatus());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid vehicle status value: " + vehicleDTO.getVehicleStatus());
            }
        }
        if (vehicleDTO.getChassisNumber() != null && !vehicleDTO.getChassisNumber().equals(existingVehicle.getChassisNumber())) {
            existingVehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        }
        if (vehicleDTO.getEngineNumber() != null && !vehicleDTO.getEngineNumber().equals(existingVehicle.getEngineNumber())) {
            existingVehicle.setEngineNumber(vehicleDTO.getEngineNumber());
        }
        if (vehicleDTO.getKeyNumber() != null && !vehicleDTO.getKeyNumber().equals(existingVehicle.getKeyNumber())) {
            existingVehicle.setKeyNumber(vehicleDTO.getKeyNumber());
        }
        if (vehicleDTO.getInvoiceDate() != null && !vehicleDTO.getInvoiceDate().equals(existingVehicle.getInvoiceDate())) {
            existingVehicle.setInvoiceDate(vehicleDTO.getInvoiceDate());
        }
        if (vehicleDTO.getInvoiceNumber() != null && !vehicleDTO.getInvoiceNumber().equals(existingVehicle.getInvoiceNumber())) {
            existingVehicle.setInvoiceNumber(vehicleDTO.getInvoiceNumber());
        }
        if (vehicleDTO.getPurchaseDealer() != null && !vehicleDTO.getPurchaseDealer().equals(existingVehicle.getPurchaseDealer())) {
            existingVehicle.setPurchaseDealer(vehicleDTO.getPurchaseDealer());
        }
        if (vehicleDTO.getManufactureDate() != null && !vehicleDTO.getManufactureDate().equals(existingVehicle.getManufactureDate())) {
            existingVehicle.setManufactureDate(vehicleDTO.getManufactureDate());
        }
        if (vehicleDTO.getSuffix() != null && !vehicleDTO.getSuffix().equals(existingVehicle.getSuffix())) {
            existingVehicle.setSuffix(vehicleDTO.getSuffix());
        }
        if (vehicleDTO.getInvoiceValue() != null && !vehicleDTO.getInvoiceValue().equals(existingVehicle.getInvoiceValue())) {
            existingVehicle.setInvoiceValue(vehicleDTO.getInvoiceValue());
        }
        if (vehicleDTO.getReceivedDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(vehicleDTO.getReceivedDate()); // Convert Date to String
            LocalDate receivedDate = LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")); // Parse to LocalDate
            if (!receivedDate.equals(existingVehicle.getReceivedDate())) {
                existingVehicle.setReceivedDate(java.sql.Date.valueOf(receivedDate)); // Convert LocalDate to java.sql.Date
            }
        }
        if (vehicleDTO.getAge() != null && !vehicleDTO.getAge().equals(existingVehicle.getAge())) {
            existingVehicle.setAge(vehicleDTO.getAge());
        }
        if (vehicleDTO.getInterest() != null && !vehicleDTO.getInterest().equals(existingVehicle.getInterest())) {
            existingVehicle.setInterest(vehicleDTO.getInterest());
        }
        if (vehicleDTO.getRemarks() != null && !vehicleDTO.getRemarks().equals(existingVehicle.getRemarks())) {
            existingVehicle.setRemarks(vehicleDTO.getRemarks());
        }
        try {
            existingVehicle.setUpdatedBy(loggedInUser);
            Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
            return vehicleMapper.toDTO(updatedVehicle);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("chassisNumber")) {
                throw new ConstraintViolationException("A vehicle with the same chassis number already exists.");
            }
            throw new RuntimeException("An error occurred while updating the vehicle.", ex);
        }

    }

    public void deleteVehicle(Long id) throws VehicleNotFoundException {
        if (vehicleRepository.findById(id).isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Map<String, Map<String, Long>> getAgeCountByModel() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Filter out vehicles with null models
        List<Vehicle> validVehicles = vehicles.stream()
                .filter(vehicle -> vehicle.getModel() != null)
                .collect(Collectors.toList());

        return validVehicles.stream()
                .collect(Collectors.groupingBy(
                        Vehicle::getModel, // Group by model
                        Collectors.groupingBy(vehicle -> {
                            Integer age = VehicleUtils.calculateVehicleAge(String.valueOf(vehicle.getReceivedDate()));
                            if (age == null) {
                                return "Unknown";
                            } else if (age < 30) {
                                return "Less than 30 days";
                            } else if (age <= 60) {
                                return "30 to 60 days";
                            } else {
                                return "Greater than 60 days";
                            }
                        }, Collectors.counting()) // Count vehicles in each age range
                ));
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    public List<VehicleDTO> getAllUniqueVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Filter out vehicles with null models
        List<Vehicle> validVehicles = vehicles.stream()
                .filter(vehicle -> vehicle.getModel() != null)
                .collect(Collectors.toList());

        // Group vehicles by model and calculate age counts
        Map<String, Map<String, Long>> ageCountsByModel = validVehicles.stream()
                .collect(Collectors.groupingBy(
                        Vehicle::getModel,
                        Collectors.groupingBy(vehicle -> {
                            Integer age = VehicleUtils.calculateVehicleAge(String.valueOf(vehicle.getInvoiceDate()));
                            if (age == null) {
                                return "Unknown";
                            } else if (age < 30) {
                                return "Less than 30 days";
                            } else if (age <= 60) {
                                return "30 to 60 days";
                            } else {
                                return "Greater than 60 days";
                            }
                        }, Collectors.counting())
                ));

        // Map unique vehicles to DTOs and set age counts
        return validVehicles.stream()
                .filter(distinctByKey(Vehicle::getModel)) // Unique by model
                .map(vehicle -> {
                    VehicleDTO dto = vehicleMapper.toDTO(vehicle);

                    Map<String, Long> ageCounts = ageCountsByModel.getOrDefault(vehicle.getModel(), Map.of());
                    dto.setLessThan30DaysCount(ageCounts.getOrDefault("Less than 30 days", 0L));
                    dto.setBetween30And60DaysCount(ageCounts.getOrDefault("30 to 60 days", 0L));
                    dto.setGreaterThan60DaysCount(ageCounts.getOrDefault("Greater than 60 days", 0L));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void saveVehiclesFromFile(MultipartFile file) throws Exception {
        String loggedInUser = userDetailsService.getLoggedInUsername();
        String fileName = file.getOriginalFilename();
        if (fileName.isBlank() || !fileName.endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid file format. Please upload a CSV file.");
        }

        logger.info("Processing file: {}", fileName);

        List<Vehicle> vehicles = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] headers = csvReader.readNext(); // Read header row
            logger.info("CSV Headers: {}", Arrays.toString(headers));

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                logger.info("Processing row: {}", Arrays.toString(row));
                Vehicle vehicle = new Vehicle();
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i];
                    String value = row[i];

                    Field field = Vehicle.class.getDeclaredField(header);
                    field.setAccessible(true);

                    // Special handling for the "location" field
                    if ("location".equalsIgnoreCase(header)) {
                        field.set(vehicle, value != null && !value.isBlank() ? value.toUpperCase() : null);
                    } else {
                        field.set(vehicle, parseValue(field, value));
                    }
                }
                vehicle.setCreatedBy(loggedInUser); // Set createdBy to logged-in user
                vehicles.add(vehicle);
            }
        }

        logger.info("Saving {} vehicles to the database.", vehicles.size());
        vehicleRepository.saveAll(vehicles);
    }

    private String getRowData(Row row) {
        if (row == null) {
            return "null";
        }
        StringBuilder rowData = new StringBuilder();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            rowData.append(cell != null ? cell.toString() : "null").append(", ");
        }
        return rowData.toString();
    }

    private Object parseValue(Field field, String value) throws Exception {
        if (field.getType().isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum> enumType = (Class<? extends Enum>) field.getType();
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } else if (field.getType().equals(Double.class)) {
            return Double.parseDouble(value);
        } else if (field.getType().equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (field.getType().equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (field.getType().equals(Date.class)) {
            List<String> dateFormats = Arrays.asList("yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "dd/MM/yyyy", "dd/MM/yy");
            for (String format : dateFormats) {
                try {
                    logger.info("Attempting to parse value '{}' with format '{}'", value, format);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                    dateFormat.setLenient(false); // Strict parsing
                    Date parsedDate = dateFormat.parse(value.trim());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    int year = calendar.get(Calendar.YEAR);
                    if (year < 100) { // Handle two-digit year
                        calendar.set(Calendar.YEAR, year + 2000); // Adjust to 21st century
                        parsedDate = calendar.getTime();
                    }
                    logger.info("Successfully parsed value '{}' as date '{}'", value, parsedDate);
                    return parsedDate;
                } catch (Exception ignored) {
                    logger.warn("Failed to parse value '{}' with format '{}'", value, format);
                }
            }
            throw new IllegalArgumentException("Invalid date format for field: " + field.getName());
        } else {
            return value; // Default to String
        }
    }

    private Object parseCellValue(Field field, Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (field.getType().equals(Integer.class)) {
                    return (int) cell.getNumericCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return null;
        }
    }

    public List<VehicleOrderResponse> getVehicleAndOrderDetailsByModel(String model) {
        try {
            log.info("Entering getVehicleAndOrderDetailsByModel with model: {}", model);
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Model parameter cannot be null or empty");
            }
            List<VehicleOrderResponse> response = vehicleRepository.findVehicleAndOrderDetailsByModel(model);
            log.info("Successfully retrieved vehicle and order details for model: {}", model);
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for model: {}", model, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while fetching vehicle and order details for model: {}", model, e);
            throw e;
        } finally {
            log.info("Exiting getVehicleAndOrderDetailsByModel");
        }
    }

    public void saveTestDrivesFromFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank() || !fileName.endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid file format. Please upload a CSV file.");
        }

        List<TestDrive> testDrives = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] headers = csvReader.readNext(); // Read header row
            if (headers == null) {
                throw new IllegalArgumentException("The file is empty or missing headers.");
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                TestDrive testDrive = new TestDrive();
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i];
                    String value = row[i];

                    try {
                        Field field = TestDrive.class.getDeclaredField(header);
                        field.setAccessible(true);
                        field.set(testDrive, parseValue(field, value));
                    } catch (NoSuchFieldException e) {
                        // If the header does not match any field, set the value to null
                        log.warn("Unmatched column '{}' in CSV file. Setting to null.", header);
                    }
                }
                testDrives.add(testDrive);
            }
        }

        testDriveRepository.saveAll(testDrives);
    }

    public List<TestDrive> getAllTestDrives() {
        List<TestDrive> testDrives = testDriveRepository.findAll();
        if (testDrives.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no test drives found
        }
        return testDrives.stream()
                .map(testDrive -> {
                    TestDrive dto = new TestDrive();
                    dto.setId(testDrive.getId());
                    dto.setMake(testDrive.getMake());
                    dto.setModel(testDrive.getModel());
                    dto.setChassisNumber(testDrive.getChassisNumber());
                    dto.setKeyNumber(testDrive.getKeyNumber());
                    dto.setEngineNumber(testDrive.getEngineNumber());
                    dto.setExteriorColor(testDrive.getExteriorColor());
                    dto.setInteriorColor(testDrive.getInteriorColor());
                    dto.setGrade(testDrive.getGrade());
                    dto.setLocation(testDrive.getLocation() != null ? testDrive.getLocation().toUpperCase() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}