package com.swd392.mentorbooking.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupRequest {
    private Long topicId;
    List<Long> studentIds;
}
