package com.swd392.mentorbooking.dto.projectprogress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnCreateRequestDTO {
    private Long id;

    private Long projectProgressId;

    private String title;

    private Integer orderIndex;
}
