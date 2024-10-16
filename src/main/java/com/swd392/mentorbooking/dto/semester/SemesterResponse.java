package com.swd392.mentorbooking.dto.semester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterResponse {
    private long semesterId;
    private String semesterName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
