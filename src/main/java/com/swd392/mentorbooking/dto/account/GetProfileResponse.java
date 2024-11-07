package com.swd392.mentorbooking.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swd392.mentorbooking.dto.achievement.GetAchievementResponseDTO;
import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProfileResponse {
    private Long id;
    private String name;
    private String email;
    private RoleEnum role;
    private LocalDateTime dayOfBirth;
    private GenderEnum gender;
    private String phone;
    private String avatar;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String className;
    // ** wallet part ** //
    private double walletId;
    private double walletPoint;
    // ** mentor part ** //
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private double servicePrice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SpecializationEnum> specializations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<GetAchievementResponseDTO> achievements;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String facebookLink;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String linkedinLink;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String twitterLink;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String youtubeLink;
}
