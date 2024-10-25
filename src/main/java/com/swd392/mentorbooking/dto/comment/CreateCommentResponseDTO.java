package com.swd392.mentorbooking.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CreateCommentResponseDTO {
    private long commentId;
    private long blogId;
    private String comment;
    private String author;
}
