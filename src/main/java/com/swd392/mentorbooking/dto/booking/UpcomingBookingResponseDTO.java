package com.swd392.mentorbooking.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpcomingBookingResponseDTO {
    private Long bookingId;
    @JsonFormat(pattern="dd-MM-yyyy")
    private String bookingDate;
    private String startTime;
    private String endTime;
    private String location;
    private String locationNote;
    private Long mentorId;
    private String mentorName;
    private String mentorAvatar;
    private double mentorPrice;
    private BookingStatus bookingStatus;
    private List<SpecializationEnum> mentorSpecializations;
}
