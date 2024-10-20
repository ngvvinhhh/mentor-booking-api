package com.swd392.mentorbooking.dto.booking;

import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingListResponseDTO {
    private Long bookingId;
    private String location;
    private String note;
    private Date bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingMentorResponseDTO mentor;
    private List<BookingGroupResponseDTO> group;
    private BookingStatus status;
}
