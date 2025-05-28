package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AnalyticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public AnalyticsResponse fetchMonthlySales(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT DATE(o.order_date) AS date, " +
                        "v.model, " +
                        "SUM(v.tkm_invoice_value) AS totalSales " + // Use SUM for aggregation
                        "FROM orders o " +
                        "JOIN vehicle v ON o.vehicle_id = v.id " +
                        "WHERE o.order_date BETWEEN :startDate AND :endDate "
        );

        if (filters.containsKey("brandName") && filters.get("brandName") != null && !filters.get("brandName").isEmpty()) {
            queryBuilder.append("AND v.make = :brandName ");
        }

        if (filters.containsKey("modelName") && filters.get("modelName") != null && !filters.get("modelName").isEmpty()) {
            queryBuilder.append("AND v.model = :modelName ");
        }

        queryBuilder.append("GROUP BY DATE(o.order_date), v.model ");
        queryBuilder.append("ORDER BY DATE(o.order_date)");

        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString());

        nativeQuery.setParameter("startDate", filters.get("startDate"));
        nativeQuery.setParameter("endDate", filters.get("endDate"));

        if (filters.containsKey("brandName") && filters.get("brandName") != null && !filters.get("brandName").isEmpty()) {
            nativeQuery.setParameter("brandName", filters.get("brandName"));
        }
        if (filters.containsKey("modelName") && filters.get("modelName") != null && !filters.get("modelName").isEmpty()) {
            nativeQuery.setParameter("modelName", filters.get("modelName"));
        }

        List<Object[]> results = nativeQuery.getResultList();
        List<AnalyticsResponse.Data> dataList = results.stream()
                .map(result -> new AnalyticsResponse.Data(
                        result[0].toString(), // date
                        result[1].toString(), // model
                        Double.parseDouble(result[2].toString()) // totalSales
                ))
                .toList();

        return new AnalyticsResponse(dataList);
    }

    public AnalyticsResponse fetchTopModelSold(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT v.model AS modelName, COUNT(o.order_id) AS salesCount " +
                        "FROM orders o " +
                        "JOIN vehicle v ON o.vehicle_id = v.id " +
                       // "JOIN city c ON v.location = c.name " +
                        "WHERE o.order_date BETWEEN :startDate AND :endDate "
        );

        // Add city filter if provided
//        if (filters.containsKey("city") && filters.get("city") != null && !filters.get("city").isEmpty()) {
//            queryBuilder.append("AND c.name = :city ");
//        }

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