package com.swd392.mentorbooking.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationResponse {
    private Long id;

    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    private BookingStatus status;

    private Long bookingId;

    private Long invitationId;
}
