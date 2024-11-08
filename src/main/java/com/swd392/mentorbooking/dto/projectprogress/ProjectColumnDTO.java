package com.swd392.mentorbooking.dto.projectprogress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectColumnDTO {
    private String id;
    private String boardId;
    private String title;
    private List<String> cardOrderIds;
    private List<ProgressCardDTO> cards;
}
