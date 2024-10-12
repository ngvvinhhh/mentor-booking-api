package com.swd392.mentorbooking.dto.group;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateGroupResponse {

    private Long groupId;
    private Long topicId;
    List<Long> studentIds;
    private Integer quantityMember;
    private LocalDateTime updatedAt;
}
