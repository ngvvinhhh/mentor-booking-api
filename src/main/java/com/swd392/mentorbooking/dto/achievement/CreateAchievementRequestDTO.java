package com.swd392.mentorbooking.dto.achievement;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAchievementRequestDTO {
    @NotNull(message = "Please input achievement name!")
    private String achievementName;

    @NotNull(message = "Please input achievement link!")
    private String achievementLink;

    @NotNull(message = "Please input achievement description!")
    private String achievementDescription;
}
