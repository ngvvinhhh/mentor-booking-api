package com.swd392.mentorbooking.dto.otp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOTPResponseDTO {
    private String otp;
    private String email;
    private boolean isVerified;
}
