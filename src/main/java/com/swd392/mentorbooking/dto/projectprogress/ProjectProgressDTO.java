package com.swd392.mentorbooking.dto.projectprogress;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressDTO {
    private String id;
    private String title;
    private String description;
    private String type;
    private List<String> columnOrderIds;
    private List<ProjectColumnDTO> columns;
}
