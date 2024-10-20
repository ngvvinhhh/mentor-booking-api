package com.swd392.mentorbooking.dto.mentor;

import com.swd392.mentorbooking.entity.Enum.ScheduleStatus;
import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateScheduleResponseDTO {
    private Long accountId;
    private String accountName;
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleStatus status;
}
