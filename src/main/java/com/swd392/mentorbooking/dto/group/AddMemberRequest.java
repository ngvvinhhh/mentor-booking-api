package com.swd392.mentorbooking.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddMemberRequest {
    private Long groupId;
    private Long accountId;
}
