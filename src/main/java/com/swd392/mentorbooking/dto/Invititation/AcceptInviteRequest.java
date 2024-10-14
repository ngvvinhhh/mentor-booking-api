package com.swd392.mentorbooking.dto.Invititation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AcceptInviteRequest {
    @Email(message = "Email should be valid")
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Token is required")
    private String token;
}
