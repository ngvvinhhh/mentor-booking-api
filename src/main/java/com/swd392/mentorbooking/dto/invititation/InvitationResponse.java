package com.swd392.mentorbooking.dto.invititation;

import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Enum.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationResponse {
    private Long invitationId;
    private String sender;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
