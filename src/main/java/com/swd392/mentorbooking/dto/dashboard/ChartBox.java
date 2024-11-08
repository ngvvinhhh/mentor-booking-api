package com.swd392.mentorbooking.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO cho chartBoxUser, chartBoxProduct, chartBoxRevenue, chartBoxConversion
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartBox {
    private String color;
    private String icon;
    private String title;
    private String number;
    private String dataKey;
    private int percentage;
    private List<ChartData> chartData;
}
