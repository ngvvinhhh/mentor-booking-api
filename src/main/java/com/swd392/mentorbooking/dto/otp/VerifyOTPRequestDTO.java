package com.swd392.mentorbooking.dto.otp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPRequestDTO {
    private String otp;
    private String email;
}
