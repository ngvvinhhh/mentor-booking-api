package com.swd392.mentorbooking.dto.booking;

import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpcomingBookingResponseDTO {
    private Long bookingId;
    private String bookingDate;
    private String startTime;
    private String endTime;
    private String location;
    private String locationNote;
    private Long mentorId;
    private String mentorName;
    private BookingStatus bookingStatus;
}
