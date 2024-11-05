package com.swd392.mentorbooking.dto.mentor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.ScheduleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
public class GetScheduleResponseDTO {

    private long scheduleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleStatus status;
    private LocalDateTime createdAt;
    private Boolean isDeleted;

}
