package com.swd392.mentorbooking.dto.achievement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAchievementResponseDTO {
    private Long achievementId;

    private String achievementName;

    private String achievementLink;

    private String achievementDescription;

    private LocalDateTime created_at;

    private LocalDateTime update_at;
}
