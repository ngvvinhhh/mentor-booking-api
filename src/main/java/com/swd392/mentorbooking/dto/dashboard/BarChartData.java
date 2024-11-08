package com.swd392.mentorbooking.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO cho dữ liệu trong barChartData
@Data
@AllArgsConstructor
public class BarChartData {
    private String name;
    private int value;

    // Getters and Setters
}
