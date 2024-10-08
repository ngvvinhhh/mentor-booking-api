package com.swd392.mentorbooking.dto.auth;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordResponse {
    private String message;
    private String error;
    private Integer code;
}
