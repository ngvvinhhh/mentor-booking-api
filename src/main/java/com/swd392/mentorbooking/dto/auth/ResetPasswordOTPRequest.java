package com.swd392.mentorbooking.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordOTPRequest {
    private String email;
    private String otp;
    private String newPassword;
    private String repeatPassword;
}
