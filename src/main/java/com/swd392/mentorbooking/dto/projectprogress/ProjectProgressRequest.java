package com.swd392.mentorbooking.dto.projectprogress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProgressRequest {
    private Long groupId;
    private String description;
}