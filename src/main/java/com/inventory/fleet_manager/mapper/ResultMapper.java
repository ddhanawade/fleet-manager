package com.inventory.fleet_manager.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class ResultMapper {

    public static <T> T mapToDTO(Object[] result, String[] columnAliases, Class<T> dtoClass) {
        try {
            T dto = dtoClass.getDeclaredConstructor().newInstance();
            Field[] fields = dtoClass.getDeclaredFields();

            for (int i = 0; i < columnAliases.length; i++) {
                String columnAlias = columnAliases[i];
                Object value = result[i];

                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase(columnAlias)) {
                        field.setAccessible(true);
                        try {
                            // Convert value to the field's type if necessary
                            Object convertedValue = convertValue(value, field.getType());
                            field.set(dto, convertedValue);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Error setting field '" + field.getName() + "' with value '" + value + "' of type '" + (value != null ? value.getClass() : "null") + "'");
                            throw e;
                        }
                        break;
                    }
                }
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping result to DTO for class: " + dtoClass.getName(), e);
        }
    }

    public static <T> List<T> mapToDTOList(List<Object[]> results, String[] columnAliases, Class<T> dtoClass) {
        return results.stream()
                .map(result -> mapToDTO(result, columnAliases, dtoClass))
                .collect(Collectors.toList());
    }

    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        try {
            if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(value.toString());
            }
            if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(value.toString());
            }
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(value.toString());
            }
            if (targetType == String.class) {
                return value.toString();
            }
            if (targetType == java.util.Date.class) {
                if (value instanceof java.sql.Timestamp) {
                    return new java.util.Date(((java.sql.Timestamp) value).getTime());
                }
                if (value instanceof java.sql.Date) {
                    return new java.util.Date(((java.sql.Date) value).getTime());
                }
            }
            if (targetType == java.time.LocalDate.class) {
                if (value instanceof java.sql.Timestamp) {
                    return ((java.sql.Timestamp) value).toLocalDateTime().toLocalDate();
                }
                if (value instanceof java.sql.Date) {
                    return ((java.sql.Date) value).toLocalDate();
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot convert value '" + value + "' to type " + targetType.getName(), e);
        }
        throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
    }
}