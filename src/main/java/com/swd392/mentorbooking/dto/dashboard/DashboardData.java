package com.swd392.mentorbooking.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardData {

    private List<TopDealUser> topDealUsers;
    private ChartBox chartBoxUser;
    private ChartBox chartBoxProduct;
    private ChartBox chartBoxRevenue;
    private ChartBox chartBoxConversion;
    private BarChartBox barChartBoxRevenue;
    private BarChartBox barChartBoxVisit;
    private List<UserRow> userRows;

}
