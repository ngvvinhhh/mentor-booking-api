package com.swd392.mentorbooking.dto.account;

import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import lombok.*;

import java.util.List;

@Setter
@Getter
public class SearchMentorResponseDTO {
    private Long accountId;
    private String accountName;
    private String accountEmail;
    private String accountPhone;
    private double pricePerHour;
    private String avatar;
    private List<SpecializationEnum> specializationList;
}
