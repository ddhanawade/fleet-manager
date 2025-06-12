package com.inventory.fleet_manager.service;

import com.inventory.fleet_manager.dto.ModelInfoDTO;
import com.inventory.fleet_manager.dto.VehicleDTO;
import com.inventory.fleet_manager.dto.VehicleOrderResponse;
import com.inventory.fleet_manager.exception.VehicleNotFoundException;
import com.inventory.fleet_manager.mapper.VehicleMapper;
import com.inventory.fleet_manager.model.Vehicle;
import com.inventory.fleet_manager.repository.ModelRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
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

@Service
@Slf4j
public class ModelService {
    private static final Logger logger = LoggerFactory.getLogger(ModelService.class);

  private final ModelRepository modelRepo;

    public ModelService(ModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    public List<ModelInfoDTO> getAllModelInfo() {
        try {
            log.info("Entering getAllModelInfo");
            List<ModelInfoDTO> modelInfoList = modelRepo.findAll().stream()
                    .map(model -> new ModelInfoDTO(model.getId(), model.getMake(), model.getModel()))
                    .collect(Collectors.toList());
            log.info("Successfully retrieved model information");
            return modelInfoList;
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while fetching model information", e);
            throw e;
        } finally {
            log.info("Exiting getAllModelInfo");
        }
    }
}