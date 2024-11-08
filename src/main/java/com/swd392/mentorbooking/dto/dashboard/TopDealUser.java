package com.swd392.mentorbooking.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO cho topDealUsers
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopDealUser {
    private long id;
    private String img;
    private String username;
    private String email;
    private double amount;

    // Getters and Setters
}
