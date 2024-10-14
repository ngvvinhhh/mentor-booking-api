package com.swd392.mentorbooking.dto.account.student;

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
    private String className;
    // mentor part //
    private List<SpecializationEnum> specializations;
    private String facebookLink;
    private String linkedinLink;
    private String twitterLink;
    private String youtubeLink;
}
