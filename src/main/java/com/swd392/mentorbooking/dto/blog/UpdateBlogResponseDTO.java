package com.swd392.mentorbooking.dto.blog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UpdateBlogResponseDTO {
    private String title;
    private String description;
    private String image;
    private LocalDateTime updatedAt;
    private Boolean is_deleted;
}
