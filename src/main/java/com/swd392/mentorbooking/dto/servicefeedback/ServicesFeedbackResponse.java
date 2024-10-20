package com.swd392.mentorbooking.dto.servicefeedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicesFeedbackResponse {
    private Long id;

    private Long serviceId;

    private Integer rating;

    private String description;

    private LocalDateTime createdAt;
}
