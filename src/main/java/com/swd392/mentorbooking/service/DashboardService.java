package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.dashboard.ChartBox;
import com.swd392.mentorbooking.dto.dashboard.DashboardData;
import com.swd392.mentorbooking.dto.dashboard.TopDealUser;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.WalletLogRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<TopDealUser> getTopDealUsers() {
        List<TopDealUser> topDealUsers = walletLogRepository.findTop7UsersByAmount();
        return topDealUsers;
    }

    public ChartBox getChartBoxUser() {
        ChartBox chartBox = new ChartBox();
        chartBox.setColor("#8884d8");
        chartBox.setIcon("/userIcon.svg");
        chartBox.setTitle("Total Users");
        chartBox.setDataKey("users");
        chartBox.setPercentage(20);
        chartBox.setNumber((accountRepository.countTotalUsers().orElse(0).toString()));
        chartBox.setChartData(new ArrayList<>());
        return chartBox;
    }

    public ChartBox getChartBoxMentor() {
        ChartBox chartBox = new ChartBox();
        chartBox.setColor("skyblue");
        chartBox.setIcon("/productIcon.svg");
        chartBox.setTitle("Total Mentors");
        chartBox.setDataKey("products");
        chartBox.setPercentage(20);
        chartBox.setNumber((accountRepository.countTotalMentors().orElse(0).toString()));
        chartBox.setChartData(new ArrayList<>());
        return chartBox;
    }

    public ChartBox getChartBoxStudent() {
        ChartBox chartBox = new ChartBox();
        chartBox.setColor("gold");
        chartBox.setIcon("/conversionIcon.svg");
        chartBox.setTitle("Total Students");
        chartBox.setDataKey("ratio");
        chartBox.setPercentage(20);
        chartBox.setNumber((accountRepository.countTotalStudents().orElse(0).toString()));
        chartBox.setChartData(new ArrayList<>());
        return chartBox;
    }

    public ChartBox getTotalRevenue() {
        ChartBox chartBox = new ChartBox();
        chartBox.setColor("teal");
        chartBox.setIcon("/revenueIcon.svg");
        chartBox.setTitle("Total Revenue");
        chartBox.setDataKey("ratio");
        chartBox.setPercentage(20);
        chartBox.setNumber((accountRepository.countTotalStudents().orElse(0).toString()));
        chartBox.setChartData(new ArrayList<>());
        return chartBox;
    }
}
