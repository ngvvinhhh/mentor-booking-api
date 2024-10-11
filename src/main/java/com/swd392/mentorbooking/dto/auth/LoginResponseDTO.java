package com.swd392.mentorbooking.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private int code;

    private String message;

    private String error;

    private String accessToken;

    private String refreshToken;

    public LoginResponseDTO(int code, String message, String error, String accessToken, String refreshToken) {
        super();
        this.code = code;
        this.message = message;
        this.error = error;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
