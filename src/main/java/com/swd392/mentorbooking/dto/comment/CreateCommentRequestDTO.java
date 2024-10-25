package com.swd392.mentorbooking.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CreateCommentRequestDTO {
    private String comment;
}
