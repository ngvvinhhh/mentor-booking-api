package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.dashboard.*;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.BookingRepository;
import com.swd392.mentorbooking.repository.WalletLogRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BookingRepository bookingRepository;

    public List<TopDealUser> getTopDealUsers() {
        return walletLogRepository.findTop7UsersByAmount();
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
        chartBox.setNumber(bookingRepository.findTotalRevenueOfCompletedBookings());
        chartBox.setChartData(new ArrayList<>());
        return chartBox;
    }

    public BarChartBox getWeeklyRevenue() {
        // Lấy ngày hiện tại
        LocalDate now = LocalDate.now();

        // Tìm ngày Chủ nhật đầu tuần và Thứ bảy cuối tuần
        LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate endOfWeek = startOfWeek.plusDays(7);

        // Chuyển đổi sang LocalDateTime
        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekDateTime = endOfWeek.atStartOfDay();

        // Lấy dữ liệu từ repository
        List<Object[]> weeklyData = bookingRepository.findWeeklyRevenue(startOfWeekDateTime, endOfWeekDateTime);

        // Tạo danh sách BarChartData
        List<BarChartData> chartDataList = new ArrayList<>();
        for (Object[] data : weeklyData) {
            int dayOfWeek = (int) data[0]; // Số ngày trong tuần
            double dailyRevenue = (double) data[1]; // Doanh thu trong ngày

            // Lấy tên ngày trong tuần từ số thứ tự
            String dayName = DayOfWeek.of(dayOfWeek % 7 + 1) // dayOfWeek (1 = Chủ nhật) trong MySQL
                    .getDisplayName(TextStyle.FULL, Locale.getDefault());

            // Thêm BarChartData vào danh sách
            chartDataList.add(new BarChartData(dayName, (int) dailyRevenue));
        }

        // Tạo BarChartBox và gán danh sách BarChartData
        BarChartBox barChartBox = new BarChartBox();
        barChartBox.setChartData(chartDataList);

        return barChartBox;
    }
}
