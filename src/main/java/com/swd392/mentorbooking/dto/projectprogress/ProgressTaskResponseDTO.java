package com.swd392.mentorbooking.dto.projectprogress;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.TaskPriorityEnum;
import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import com.swd392.mentorbooking.entity.ProjectProgress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressTaskResponseDTO {
    private Long taskId;

    private Long projectProgressId;

    private String taskKey;

    private String taskSummary;

    private TaskStatusEnum taskStatus;

    private String assigneeName;

    private String assigneeAvatar;

    private LocalDate dueDate;

    private TaskPriorityEnum taskPriority;
}
