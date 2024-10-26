package com.swd392.mentorbooking.dto.topic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTopicRequest {
    private String topicName;

    private String description;

}
