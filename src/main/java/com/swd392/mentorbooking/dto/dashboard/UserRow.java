package com.swd392.mentorbooking.dto.dashboard;

import lombok.Data;

// DTO cho userRows
@Data
public class UserRow {
    private int id;
    private String img;
    private String lastName;
    private String firstName;
    private String email;
    private String phone;
    private String createdAt;
    private boolean verified;

    // Getters and Setters
}
