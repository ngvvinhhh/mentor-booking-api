package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.notification.NotificationResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Notification;
import com.swd392.mentorbooking.repository.NotificationRepository;
import com.swd392.mentorbooking.repository.BookingRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Response<List<NotificationResponse>> getAllNotifications() {
        // Get current account
        Account currentAccount = accountUtils.getCurrentAccount();
        if (currentAccount == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Retrieve all bookings for the current account
        List<Booking> bookings = bookingRepository.findByAccount(currentAccount);
        if (bookings.isEmpty()) {
            return new Response<>(404, "No bookings found for the current account", null);
        }

        // Collect notifications for all bookings
        List<Notification> notifications = new ArrayList<>();
        for (Booking booking : bookings) {
            List<Notification> bookingNotifications = notificationRepository.findByBookingAndIsDeletedFalse(booking);
            notifications.addAll(bookingNotifications);
        }

        if (notifications.isEmpty()) {
            return new Response<>(404, "No notifications found", null);
        }

        // Map notifications to NotificationResponse DTO
        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getDate(),
                        notification.getStatus()))
                .collect(Collectors.toList());

        return new Response<>(200, "Notifications retrieved successfully!", notificationResponses);
    }
}
