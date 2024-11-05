package com.swd392.mentorbooking.dto.projectprogress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnUpdateRequestDTO {
    private String title;
    private Integer orderIndex;
}
