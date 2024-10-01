package com.swd392.mentorbooking.dto.auth;

import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String email;
    private String password;
    private String name;
    private RoleEnum role;
}
