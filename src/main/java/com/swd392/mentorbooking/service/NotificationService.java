package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.notification.NotificationResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Invitation;
import com.swd392.mentorbooking.entity.Notification;
import com.swd392.mentorbooking.repository.InvitaionRepository;
import com.swd392.mentorbooking.repository.NotificationRepository;
import com.swd392.mentorbooking.repository.BookingRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvitaionRepository invitaionRepository;

    public Response<List<NotificationResponse>> getAllNotificationsForAccount() {
        // Lấy thông tin tài khoản hiện tại
        Account currentAccount = accountUtils.getCurrentAccount();
        if (currentAccount == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Lấy tất cả thông báo liên quan đến tài khoản và chưa bị xóa
        List<Notification> notifications = notificationRepository.findByAccountAndIsDeletedFalse(currentAccount);
        if (notifications.isEmpty()) {
            return new Response<>(404, "No notifications found for the current account", null);
        }

        // Map các Notification sang NotificationResponse
        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getDate(),
                        notification.getStatus()))
                .collect(Collectors.toList());

        return new Response<>(200, "Notifications retrieved successfully", notificationResponses);
    }
}