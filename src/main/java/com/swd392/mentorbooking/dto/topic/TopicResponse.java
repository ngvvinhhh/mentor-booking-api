package com.swd392.mentorbooking.dto.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponse {
    private long topicId;
    private String topicName;
    private String description;
    private LocalDateTime createdAt;
}
