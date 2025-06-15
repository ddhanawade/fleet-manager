package com.inventory.fleet_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelInfoDTO {
    private Long id;
    private String make;
    private String model;

}
