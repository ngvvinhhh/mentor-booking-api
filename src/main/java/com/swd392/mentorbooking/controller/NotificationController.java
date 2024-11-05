package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.notification.NotificationResponse;
import com.swd392.mentorbooking.dto.push_notification.NotificationMessageDTO;
import com.swd392.mentorbooking.service.NotificationService;
import com.swd392.mentorbooking.utils.FirebasePushNotificationService;
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
    private NotificationService notificationService;

    @Autowired
    private FirebasePushNotificationService firebasePushNotificationService;

    @GetMapping("/all")
    public Response<List<NotificationResponse>> getAllNotifications() {
        return  notificationService.getAllNotificationsForAccount();
    }

    @PostMapping("/sendNotification")
    public String sendNotificationByToken(@RequestBody NotificationMessageDTO notificationMessageDTO) {
        return firebasePushNotificationService.sendNotificationByToken(notificationMessageDTO);
    }

}
