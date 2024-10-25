package com.swd392.mentorbooking.dto.group;


import com.swd392.mentorbooking.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupResponse {

    private Long groupId;
    private Long topicId;
    private List<Long> students;
    private Integer quantityMember;
    private LocalDateTime createdAt;
}
