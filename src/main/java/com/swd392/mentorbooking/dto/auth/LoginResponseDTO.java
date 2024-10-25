package com.swd392.mentorbooking.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private int code;

    private String message;

    private String error;

    private RoleEnum role;

    private String accessToken;

    private String refreshToken;

    public LoginResponseDTO(int code, String message, String error, RoleEnum role, String accessToken, String refreshToken) {
        super();
        this.code = code;
        this.message = message;
        this.error = error;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
