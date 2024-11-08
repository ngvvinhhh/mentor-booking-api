package com.swd392.mentorbooking.dto.group;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupStudentResponseDTO {
    private long accountId;
    private String accountName;
    private String accountAvatar;
    private String accountEmail;
}
