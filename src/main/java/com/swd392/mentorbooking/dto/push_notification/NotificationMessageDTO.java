package com.swd392.mentorbooking.dto.push_notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
public class NotificationMessageDTO {
    private String recipientToken;
    private String title;
    private String body;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String image;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> data;
}
