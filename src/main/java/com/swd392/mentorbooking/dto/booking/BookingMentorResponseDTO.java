package com.swd392.mentorbooking.dto.booking;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingMentorResponseDTO {
    private Long mentorId;
    private String mentorName;
}
