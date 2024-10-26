package com.swd392.mentorbooking.dto.blog;

import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBlogRespnseDTO {

    private Long blogId;
    private String title;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private BlogCategoryEnum category;
}
