package com.swd392.mentorbooking.dto.blog;

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
}
