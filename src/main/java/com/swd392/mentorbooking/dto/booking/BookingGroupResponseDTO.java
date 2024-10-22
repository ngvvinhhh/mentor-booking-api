package com.swd392.mentorbooking.dto.booking;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingGroupResponseDTO {
    private Long accountId;
    private String name;
    private String email;
}
