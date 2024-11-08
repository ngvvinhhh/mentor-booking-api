package com.swd392.mentorbooking.dto.group;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupResponse {

    private Long groupId;
    private Long topicId;
    private List<Long> students;
    private Integer quantityMember;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;

    public static GroupResponse fromGroup(Group group) {
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(group.getId());
        groupResponse.setTopicId(group.getTopic().getId());
        groupResponse.setQuantityMember(group.getQuantityMember());
        // Chuyển đổi danh sách sinh viên từ Group sang GroupResponse
        groupResponse.setStudents(
                group.getStudents().stream()
                        .map(Account::getId) // Giả sử Account có phương thức getId()
                        .collect(Collectors.toList())
        );
        return groupResponse;
    }
}
