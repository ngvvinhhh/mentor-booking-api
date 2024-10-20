package com.swd392.mentorbooking.dto.websitefeedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebsiteFeedbackResponse {
    private Long id;

    private String description;

    private LocalDateTime createdAt;
}
