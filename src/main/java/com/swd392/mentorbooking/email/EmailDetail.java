package com.swd392.mentorbooking.email;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetail {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
    private String name;
}
