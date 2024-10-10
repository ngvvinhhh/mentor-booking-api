package com.swd392.mentorbooking.dto.service;import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceResponseDTO {
    private double price;
    private String description;
    private LocalDateTime createdAt;
}
