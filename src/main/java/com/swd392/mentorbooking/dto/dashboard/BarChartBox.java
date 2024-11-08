package com.swd392.mentorbooking.dto.dashboard;

import lombok.Data;

import java.util.List;

// DTO cho barChartBoxRevenue, barChartBoxVisit
@Data
public class BarChartBox {
    private String title;
    private String color;
    private String dataKey;
    private List<BarChartData> chartData;

    // Getters and Setters
}
