package com.swd392.mentorbooking.dto.mentor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSocialLinkResponseDTO {
    private long accountId;
    private String accountEmail;
    private String youtubeLink;
    private String linkedinLink;
    private String facebookLink;
    private String twitterLink;
}
