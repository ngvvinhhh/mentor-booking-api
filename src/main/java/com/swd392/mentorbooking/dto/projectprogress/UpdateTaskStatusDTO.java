package com.swd392.mentorbooking.dto.projectprogress;

import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import lombok.*;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskStatusDTO {
    private TaskStatusEnum taskStatus;
}
