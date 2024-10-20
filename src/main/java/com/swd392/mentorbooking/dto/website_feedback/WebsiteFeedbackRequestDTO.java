package com.swd392.mentorbooking.dto.website_feedback;

import com.swd392.mentorbooking.entity.Enum.WebsiteFeedbackEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebsiteFeedbackRequestDTO {
    private String description;
    private WebsiteFeedbackEnum websiteFeedbackEnum;
}
