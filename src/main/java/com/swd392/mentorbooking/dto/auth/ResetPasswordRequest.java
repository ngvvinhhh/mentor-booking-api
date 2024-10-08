package com.swd392.mentorbooking.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {
    private String new_password;
    private String repeat_password;
}
