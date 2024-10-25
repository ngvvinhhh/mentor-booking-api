package com.swd392.mentorbooking.dto.blog;

import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlogRequestDTO {
    private String title;
    private String description;
    private String image;
    private BlogCategoryEnum blogCategoryEnum;}
