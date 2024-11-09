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
public class ProgressCardDTO {
    private long cardId;
    private String projectProgressId;
    private String projectColumnId;
    private String title;
    private String description;
    private String cover;
    private List<String> memberIds;
}
