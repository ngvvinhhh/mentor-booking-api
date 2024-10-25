package com.swd392.mentorbooking.dto.topic;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest {
    @NotBlank(message = "Topic name cannot be empty")
    private String topicName;

    @NotBlank(message = "Description cannot be empty")
    private String description;

}
