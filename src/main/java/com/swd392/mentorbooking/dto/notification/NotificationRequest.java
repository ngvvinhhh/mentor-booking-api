package com.swd392.mentorbooking.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NotificationRequest {
    private String token;
    private String title;
    private String body;
}
