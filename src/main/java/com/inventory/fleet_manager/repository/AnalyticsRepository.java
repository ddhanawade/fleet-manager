package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import com.inventory.fleet_manager.dto.MonthlySalesResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class AnalyticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<MonthlySalesResponse> getMonthlySalesReport(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT v.make, v.model, v.purchase_dealer, v.chassis_number, v.engine_number, v.key_number,  v.location, v.invoice_value, v.interest, v.vehicle_status, o.order_date " +
                        "FROM vehicle v " +
                        "JOIN orders o ON v.id = o.vehicle_id " +
                        "WHERE DATE(o.order_date) BETWEEN :startDate AND :endDate  AND v.vehicle_status = 'SOLD'"
        );

        // Add optional filters
        if (filters.containsKey("model") && filters.get("model") != null && !filters.get("model").isEmpty()) {
            queryBuilder.append("AND v.model = :model ");
        }
        if (filters.containsKey("make") && filters.get("make") != null && !filters.get("make").isEmpty()) {
            queryBuilder.append("AND v.make = :make ");
        }
        if (filters.containsKey("city") && filters.get("city") != null && !filters.get("city").isEmpty()) {
            queryBuilder.append("AND v.location = :city ");
        }

        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString());

        nativeQuery.setParameter("startDate", filters.get("startDate"));
        nativeQuery.setParameter("endDate", filters.get("endDate"));

        if (filters.containsKey("model") && filters.get("model") != null && !filters.get("model").isEmpty()) {
            nativeQuery.setParameter("model", filters.get("model"));
        }
        if (filters.containsKey("make") && filters.get("make") != null && !filters.get("make").isEmpty()) {
            nativeQuery.setParameter("make", filters.get("make"));
        }
        if (filters.containsKey("city") && filters.get("city") != null && !filters.get("city").isEmpty()) {
            nativeQuery.setParameter("city", filters.get("city"));
        }

        List<Object[]> results = nativeQuery.getResultList();

        return results.stream()
                .map(result -> {
                    MonthlySalesResponse response = new MonthlySalesResponse();
                    for (int i = 0; i < result.length; i++) {
                        switch (i) {
                            case 0 -> response.setMake(result[i].toString());
                            case 1 -> response.setModel(result[i].toString());
                            case 2 -> response.setPurchaseDealer(result[i].toString());
                            case 3 -> response.setChassisNumber(result[i].toString());
                            case 4 -> response.setEngineNumber(result[i].toString());
                            case 5 -> response.setKeyNumber(result[i].toString());
                            case 6 -> response.setLocation(result[i].toString());
                            case 7 -> response.setInvoiceValue(Double.parseDouble(String.valueOf(Double.parseDouble(result[i].toString()))));
                            case 8 -> response.setInterest(String.valueOf(Double.parseDouble(result[i].toString())));
                            case 9 -> response.setStatus(result[i].toString());
                            case 10 -> {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                                LocalDateTime dateTime = LocalDateTime.parse(result[i].toString(), formatter);
                                response.setOrderDate(dateTime.toLocalDate());
                            }
                        }
                    }
                    return response;
                })
                .toList();
    }

    public AnalyticsResponse fetchTopModelSold(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT v.model AS modelName, COUNT(o.order_id) AS salesCount " +
                        "FROM orders o " +
                        "JOIN vehicle v ON o.vehicle_id = v.id " +
                       // "JOIN city c ON v.location = c.name " +
                        "WHERE o.order_date BETWEEN :startDate AND :endDate "
        );

        if (filters.containsKey("city") && filters.get("city") != null && !filters.get("city").isEmpty()) {
            queryBuilder.append("AND c.name = :city ");
        }

        // Add make filter if provided
        if (filters.containsKey("make") && filters.get("make") != null && !filters.get("make").isEmpty()) {
            queryBuilder.append("AND v.make = :make ");
        }

        // Add model filter if provided
        if (filters.containsKey("model") && filters.get("model") != null && !filters.get("model").isEmpty()) {
            queryBuilder.append("AND v.model = :model ");
        }

        queryBuilder.append("GROUP BY v.model ");
        queryBuilder.append("ORDER BY salesCount DESC ");
        queryBuilder.append("LIMIT 3");

        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString());

        // Set required parameters
        nativeQuery.setParameter("startDate", filters.get("startDate"));
        nativeQuery.setParameter("endDate", filters.get("endDate"));

        // Set optional parameters
//        if (filters.containsKey("city") && filters.get("city") != null && !filters.get("city").isEmpty()) {
//            nativeQuery.setParameter("city", filters.get("city"));
//        }
        if (filters.containsKey("make") && filters.get("make") != null && !filters.get("make").isEmpty()) {
            nativeQuery.setParameter("make", filters.get("make"));
        }
        if (filters.containsKey("model") && filters.get("model") != null && !filters.get("model").isEmpty()) {
            nativeQuery.setParameter("model", filters.get("model"));
        }

        return new AnalyticsResponse(nativeQuery.getResultList());
    }
}