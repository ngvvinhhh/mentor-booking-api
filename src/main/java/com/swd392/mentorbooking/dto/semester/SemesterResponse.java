package com.swd392.mentorbooking.dto.semester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterResponse {
    private long semesterId;
    private String topicName;
}
