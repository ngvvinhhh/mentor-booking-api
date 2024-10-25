package com.swd392.mentorbooking.dto.invititation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AcceptInviteRequest {
    @NotNull(message = "Token is required")
    private String token;
}
