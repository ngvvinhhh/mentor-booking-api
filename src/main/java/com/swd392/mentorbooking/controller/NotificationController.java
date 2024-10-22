package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.notification.NotificationResponse;
import com.swd392.mentorbooking.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin("**")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/all")
    public Response<List<NotificationResponse>> getAllNotifications() {
        return  notificationService.getAllNotifications();
    }
}
