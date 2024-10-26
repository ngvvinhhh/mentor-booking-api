package com.swd392.mentorbooking.dto.comment;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequestDTO {
    private String comment;
}
