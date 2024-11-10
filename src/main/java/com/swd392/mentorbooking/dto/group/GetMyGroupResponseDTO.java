package com.swd392.mentorbooking.dto.group;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetMyGroupResponseDTO {
    private long groupId;
    private Long topicId;
    private int groupQuantity;
    private List<GroupStudentResponseDTO> studentList;
}
