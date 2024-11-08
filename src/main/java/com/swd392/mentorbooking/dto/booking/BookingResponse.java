package com.swd392.mentorbooking.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private String location;
    private String locationNote;
    private Long scheduleId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime startTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime endTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    private BookingStatus status;
    private String mentorName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long groupId;
}
