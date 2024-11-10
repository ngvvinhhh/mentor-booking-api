package com.swd392.mentorbooking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swd392.mentorbooking.entity.Enum.TaskPriorityEnum;
import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_task")  // Đổi tên bảng nếu cần
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "progress_id", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private ProjectProgress projectProgress; // Tham chiếu đến ProjectProgress

    @Column(name = "task_key", nullable = false)
    private String taskKey;

    @Column(name = "task_summary", nullable = false) // Để xác định thứ tự của các column
    private String taskSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatusEnum taskStatus;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = true)
    @JsonIgnore
    private Account assignee;

    @Column(name = "due_date", nullable = true)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_priority", nullable = false)
    private TaskPriorityEnum taskPriority;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}
