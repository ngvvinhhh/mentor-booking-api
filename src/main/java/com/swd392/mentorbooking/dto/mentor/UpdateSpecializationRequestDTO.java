package com.swd392.mentorbooking.dto.mentor;

import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSpecializationRequestDTO {
    private List<SpecializationEnum> enumList;
}
