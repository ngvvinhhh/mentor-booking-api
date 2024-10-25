package com.swd392.mentorbooking.dto.achievement;

import com.swd392.mentorbooking.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAchievementResponseDTO {
    private Long id;

    private String achievementName;

    private String link;

    private String description;
}
