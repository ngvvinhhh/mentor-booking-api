package com.swd392.mentorbooking.dto.semester;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRequest {
    @NotBlank(message = "Semester name cannot be empty")
    private String semesterName;

    private LocalDateTime startDate;
}
