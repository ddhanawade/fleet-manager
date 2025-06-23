package com.inventory.fleet_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AnalyticsResponse {
    private List<Data> data;

    public AnalyticsResponse(List<Data> data) {
        this.data = data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        private String date;
        private String model;
        private double totalSales;

        public Data(String date, String model, double totalSales) {
            this.date = date;
            this.model = model;
            this.totalSales = totalSales;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public double getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(double totalSales) {
            this.totalSales = totalSales;
        }
    }
}