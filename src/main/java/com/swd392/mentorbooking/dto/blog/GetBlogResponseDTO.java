package com.swd392.mentorbooking.dto.blog;

import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetBlogResponseDTO {

    private Long id;

    private Long authorId;

    private String authorName;

    private String image;

    private String title;

    private String description;

    private BlogCategoryEnum category;

    private Integer likeCount;

    private LocalDateTime createdAt;

    private Boolean isDeleted;

    private List<GetCommentResponseDTO> comments;
}
