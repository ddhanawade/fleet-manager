package com.inventory.fleet_manager.repository;

import com.inventory.fleet_manager.dto.AnalyticsResponse;
import com.inventory.fleet_manager.dto.MonthlyPurchaseResponse;
import com.inventory.fleet_manager.dto.MonthlySalesResponse;
import com.inventory.fleet_manager.mapper.ResultMapper;
import com.inventory.fleet_manager.model.Vehicle;
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

    public List<MonthlySalesResponse> getMonthlySalesReport(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT v.make AS make, v.model AS model, v.purchase_dealer AS purchaseDealer, " +
                        "v.chassis_number AS chassisNumber, v.engine_number AS engineNumber, " +
                        "v.key_number AS keyNumber, v.location AS location, v.invoice_value AS invoiceValue, " +
                        "v.interest AS interest, v.vehicle_status AS status, o.order_date AS orderDate, " +
                        "o.order_Id AS orderId, o.customer_name AS customerName, o.phone_number AS phoneNumber, " +
                        "o.lead_name AS leadName, o.sales_person_name AS salesPersonName, " +
                        "o.delivery_date AS deliveryDate, o.financer_name AS financerName, " +
                        "o.finance_type AS financeType, o.remarks AS remarks, " +
                        "o.created_at AS createdAt, o.updated_at AS updatedAt, " +
                        "o.created_by AS createdBy, o.updated_by AS updatedBy, o.order_status AS orderStatus " +
                        "FROM vehicle v " +
                        "JOIN orders o ON v.id = o.vehicle_id " +
                        "WHERE DATE(o.order_date) BETWEEN :startDate AND :endDate AND v.vehicle_status = 'SOLD' "
        );

        addOptionalFilters(queryBuilder, filters);

        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString());
        setQueryParameters(nativeQuery, filters);

        List<Object[]> results = nativeQuery.getResultList();
        String[] columnAliases = {
                "make", "model", "purchaseDealer", "chassisNumber", "engineNumber", "keyNumber", "location",
                "invoiceValue", "interest", "status", "orderDate", "orderId", "customerName", "phoneNumber",
                "leadName", "salesPersonName", "deliveryDate", "financerName", "financeType", "remarks",
                "createdAt", "updatedAt", "createdBy", "updatedBy", "orderStatus"
        };

        return ResultMapper.mapToDTOList(results, columnAliases, MonthlySalesResponse.class);
    }

    private void addOptionalFilters(StringBuilder queryBuilder, Map<String, String> filters) {
        if (isValidFilter(filters, "model")) {
            queryBuilder.append("AND v.model = :model ");
        }
        if (isValidFilter(filters, "make")) {
            queryBuilder.append("AND v.make = :make ");
        }
        if (isValidFilter(filters, "city")) {
            queryBuilder.append("AND v.location = :city ");
        }
        if (isValidFilter(filters, "leadName")) {
            queryBuilder.append("AND o.lead_name = :leadName ");
        }
        if (isValidFilter(filters, "salesPersonName")) {
            queryBuilder.append("AND o.sales_person_name = :salesPersonName ");
        }
    }

    private void setQueryParameters(Query query, Map<String, String> filters) {
        query.setParameter("startDate", filters.get("startDate"));
        query.setParameter("endDate", filters.get("endDate"));

        if (isValidFilter(filters, "model")) {
            query.setParameter("model", filters.get("model"));
        }
        if (isValidFilter(filters, "make")) {
            query.setParameter("make", filters.get("make"));
        }
        if (isValidFilter(filters, "city")) {
            query.setParameter("city", filters.get("city"));
        }
        if (isValidFilter(filters, "leadName")) {
            query.setParameter("leadName", filters.get("leadName"));
        }
        if (isValidFilter(filters, "salesPersonName")) {
            query.setParameter("salesPersonName", filters.get("salesPersonName"));
        }
    }

    private boolean isValidFilter(Map<String, String> filters, String key) {
        return filters.containsKey(key) && filters.get(key) != null && !filters.get(key).isEmpty();
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

    public List<MonthlyPurchaseResponse> fetchVehiclesPurchased(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder(
                "SELECT v.make AS make, v.model AS model, v.purchase_dealer AS purchaseDealer, " +
                        "v.chassis_number AS chassisNumber, v.engine_number AS engineNumber, " +
                        "v.key_number AS keyNumber, v.location AS location, v.invoice_value AS invoiceValue, " +
                        "v.interest AS interest, v.vehicle_status AS status, v.invoice_date AS invoiceDate " +
                        "FROM vehicle v " +
                        "WHERE DATE(v.invoice_date) BETWEEN :startDate AND :endDate " +
                        "AND v.vehicle_status IN ('FREE', 'AVAILABLE') "
        );
        addOptionalFilters(queryBuilder, filters);
        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString());
        setQueryParameters(nativeQuery, filters);

        List<Object[]> results = nativeQuery.getResultList();
        String[] columnAliases = {
                "make", "model", "purchaseDealer", "chassisNumber", "engineNumber", "keyNumber", "location",
                "invoiceValue", "interest", "status", "invoiceDate"
        };
        return ResultMapper.mapToDTOList(results, columnAliases, MonthlyPurchaseResponse.class);
    }
}