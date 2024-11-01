package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.notification.NotificationResponse;
import com.swd392.mentorbooking.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/all")
    public Response<List<NotificationResponse>> getAllNotifications() {
        return  notificationService.getAllNotificationsForAccount();
    }
}
