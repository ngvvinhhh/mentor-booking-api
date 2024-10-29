package com.swd392.mentorbooking.dto.mentor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateScheduleRequestDTO {

    private Date date;

    @JsonProperty("startFrom")
    private LocalTime startFrom;

    @JsonProperty("endAt")
    private LocalTime endAt;
}
