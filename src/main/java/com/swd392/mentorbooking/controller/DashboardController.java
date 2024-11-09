package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.dashboard.BarChartBox;
import com.swd392.mentorbooking.dto.dashboard.ChartBox;
import com.swd392.mentorbooking.dto.dashboard.TopDealUser;
import com.swd392.mentorbooking.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/dashboard")
@SecurityRequirement(name = "api")
//@PreAuthorize("hasAnyAuthority('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Lấy những mentor có doanh thu cao nhất nền tảng")
    @GetMapping("/top-deal-users")
    public List<TopDealUser> viewDashBoard() {
        return dashboardService.getTopDealUsers();
    }

    @Operation(summary = "Lấy tổng số người dùng")
    @GetMapping("/chart-box-user")
    public ChartBox chartBoxUser() {
        return dashboardService.getChartBoxUser();
    }

    @Operation(summary = "Lấy tổng số mentor")
    @GetMapping("/chart-box-mentor")
    public ChartBox chartBoxMentor() {
        return dashboardService.getChartBoxMentor();
    }

    @Operation(summary = "Lấy tổng số student")
    @GetMapping("/chart-box-student")
    public ChartBox chartBoxStudent() {
        return dashboardService.getChartBoxStudent();
    }

    @Operation(summary = "Lấy tổng số revenue")
    @GetMapping("/chart-box-revenue")
    public ChartBox chartBoxRevenue() {
        return dashboardService.getTotalRevenue();
    }

    @Operation(summary = "Lấy tổng số revenue theo tuần")
    @GetMapping("/weekly-revenue")
    public BarChartBox weeklyRevenue() {
        return dashboardService.getWeeklyRevenue();
    }
}
