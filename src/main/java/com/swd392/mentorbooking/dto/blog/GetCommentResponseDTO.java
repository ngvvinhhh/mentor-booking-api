package com.swd392.mentorbooking.dto.blog;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetCommentResponseDTO {

    private Long id;

    private Long authorId;

    private String authorName;

    private String authorAvatarUrl;

    private String description;
}
