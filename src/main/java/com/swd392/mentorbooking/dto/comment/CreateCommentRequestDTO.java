package com.swd392.mentorbooking.dto.comment;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequestDTO {
    private long blogId;
    private String comment;
}
