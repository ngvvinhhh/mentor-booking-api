package com.swd392.mentorbooking.dto.mentor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.ScheduleStatus;
import lombok.*;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleStatus status;
}
