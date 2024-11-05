package com.swd392.mentorbooking.dto.projectprogress;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateCardRequestDTO {

    private String title;
    private String description;
    private String cover;
    private Integer orderIndex;
    private Long accountId;

}
