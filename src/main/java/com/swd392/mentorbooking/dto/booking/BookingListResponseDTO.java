package com.swd392.mentorbooking.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.*;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingMentorResponseDTO mentor;
    private List<BookingGroupResponseDTO> group;
    private BookingStatus status;
}
