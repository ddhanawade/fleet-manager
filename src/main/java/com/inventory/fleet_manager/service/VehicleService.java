package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.dto.VehicleOrderResponse;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.VehicleRepository;
import com.inventory.fleet_manager.utility.VehicleUtils;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@Slf4j
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final VehicleRepository vehicleRepository;

    private final VehicleMapper vehicleMapper;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Custom thread pool

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper, VehicleUtils vehicleUtils) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }
    /**
     * Retrieves all vehicles from the repository, processes them asynchronously,
     * and returns a list of VehicleDTOs with calculated ages.
     *
     * @return List of VehicleDTOs
     */

    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Process each vehicle asynchronously with exception handling
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
        Vehicle savedVehicle = vehicleRepository.save(vehicleMapper.toEntity(vehicleDTO));
        return vehicleMapper.toDTO(savedVehicle);
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO vehicleDTO) throws VehicleNotFoundException {
        if (vehicleDTO == null) {
            throw new IllegalArgumentException("VehicleDTO cannot be null");
        }

        // Retrieve the existing vehicle
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        // Update only the fields provided in the DTO
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
            existingVehicle.setLocation(vehicleDTO.getLocation());
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
        if (vehicleDTO.getTkmInvoiceValue() != null && !vehicleDTO.getTkmInvoiceValue().equals(existingVehicle.getTkmInvoiceValue())) {
            existingVehicle.setTkmInvoiceValue(vehicleDTO.getTkmInvoiceValue());
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

        // Save the updated entity
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);

        // Map the updated entity back to DTO
        return vehicleMapper.toDTO(updatedVehicle);
    }

    public void deleteVehicle(Long id) throws VehicleNotFoundException {
        if (vehicleRepository.findById(id).isEmpty()) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Map<String, Map<String, Long>> getAgeCountByModel() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicles.stream()
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

        // Group vehicles by model and calculate age counts
        Map<String, Map<String, Long>> ageCountsByModel = vehicles.stream()
                .collect(Collectors.groupingBy(
                        Vehicle::getModel,
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
                        }, Collectors.counting())
                ));

        // Map unique vehicles to DTOs and set age counts
        return vehicles.stream()
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
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name is missing.");
        }

        logger.info("Processing file: {}", fileName);

        List<Vehicle> vehicles = new ArrayList<>();

        if (fileName.endsWith(".csv")) {
            // Handle CSV file
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
                        field.set(vehicle, parseValue(field, value));
                    }
                    vehicles.add(vehicle);
                }
            }
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            // Handle Excel file
            try (InputStream inputStream = file.getInputStream();
                 Workbook workbook = fileName.endsWith(".xlsx") ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                Row headerRow = sheet.getRow(0); // First row contains headers
                logger.info("Excel Headers: {}", getRowData(headerRow));

                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                    Row row = sheet.getRow(i);
                    logger.info("Processing row: {}", getRowData(row));
                    Vehicle vehicle = new Vehicle();

                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                        String header = headerRow.getCell(j).getStringCellValue();
                        Cell cell = row.getCell(j);

                        Field field = Vehicle.class.getDeclaredField(header);
                        field.setAccessible(true);
                        field.set(vehicle, parseCellValue(field, cell));
                    }
                    vehicles.add(vehicle);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload a CSV, XLS, or XLSX file.");
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
            List<String> dateFormats = Arrays.asList("yyyy-MM-dd", "MM/dd/yyyy", "dd-MM-yyyy", "dd/MM/yyyy");
            for (String format : dateFormats) {
                try {
                    logger.info("Attempting to parse value '{}' with format '{}'", value, format);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                    dateFormat.setLenient(false); // Strict parsing
                    Date parsedDate = dateFormat.parse(value.trim());
                    logger.info("Successfully parsed value '{}' as date '{}'", value, parsedDate);
                    // Convert to the desired format (e.g., yyyy-MM-dd)
                    SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return desiredFormat.parse(desiredFormat.format(parsedDate));
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
}