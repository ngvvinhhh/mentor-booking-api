package com.swd392.mentorbooking.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlogResponseDTO {
    private String title;
    private String description;
    private String image;
    private LocalDateTime updatedAt;
}
