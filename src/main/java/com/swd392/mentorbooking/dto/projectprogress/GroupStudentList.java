package com.swd392.mentorbooking.dto.projectprogress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupStudentList {
    private long accountId;
    private String accountName;
    private String accountAvatar;
    private String accountEmail;
}
