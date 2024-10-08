package com.swd392.mentorbooking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    private String error;
    private Integer code;

}
