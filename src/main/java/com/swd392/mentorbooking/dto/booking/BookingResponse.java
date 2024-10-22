package com.swd392.mentorbooking.dto.booking;

import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private String location;
    private String locationNote;
    private Long scheduleId;
    private String message;
    private BookingStatus status;
    private String mentorName;
}
