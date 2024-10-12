package com.swd392.mentorbooking.dto.account.mentor;

import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetMentorProfileResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime dayOfBirth;
    private GenderEnum gender;
    private String phone;
    private String avatar;
    private List<SpecializationEnum> specializations = null;
    private String youtubeLink;
    private String linkedinLink;
    private String facebookLink;
    private String twitterLink;

}
