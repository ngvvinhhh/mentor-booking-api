package com.swd392.mentorbooking.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequestDTO {
    private Long accountId;
    private String name;
    private String email;
    private String phone;
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDateTime dayOfBirth;
    private GenderEnum gender;
    private String avatar;
    private String className;
    private String youtubeLink;
    private String linkedinLink;
    private String facebookLink;
    private String twitterLink;
}
