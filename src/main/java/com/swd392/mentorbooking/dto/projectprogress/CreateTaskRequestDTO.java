package com.swd392.mentorbooking.dto.projectprogress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swd392.mentorbooking.entity.Enum.TaskPriorityEnum;
import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskRequestDTO {
    @NotNull(message = "Task summary is required")
    private String taskSummary;
    private TaskStatusEnum taskStatus;
    private Long assigneeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private TaskPriorityEnum taskPriority;
}
