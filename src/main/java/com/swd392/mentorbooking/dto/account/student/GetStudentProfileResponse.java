package com.swd392.mentorbooking.dto.account.student;

import com.swd392.mentorbooking.entity.Enum.GenderEnum;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStudentProfileResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime dayOfBirth;
    private GenderEnum gender;
    private String phone;
    private String avatar;
    private String className;
}
