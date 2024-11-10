package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.projectprogress.*;
import com.swd392.mentorbooking.entity.Enum.TaskPriorityEnum;
import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import com.swd392.mentorbooking.entity.ProgressCard;
import com.swd392.mentorbooking.entity.ProjectProgress;
import com.swd392.mentorbooking.service.ProjectProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-progress")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class ProjectProgressController {

    @Autowired
    ProjectProgressService projectProgressService;
//
//    @Operation(summary = "Hãy bỏ qua thứ này, chúng ta không đụng")
//    @PostMapping("/create")
//    public Response<ProjectProgress> createProjectProgress(@Valid @RequestBody ProjectProgressRequest projectProgressRequest) {
//        return projectProgressService.createProjectProgress(projectProgressRequest);
//    }
//
//    @Operation(summary = "Hãy bỏ qua thứ này, chúng ta không đụng")
//    @GetMapping("/view")
//    public Response<List<ProjectProgress>> getAllProjectProgress() {
//        return projectProgressService.getAllProjectProgress();
//    }
//
//    @Operation(summary = "Xem project progress của nhóm của thằng đang đăng nhập")
//    @GetMapping("/view/my-group")
//    public Response<ProjectProgressDTO> getMyProjectProgress() {
//        return projectProgressService.getMyProjectProgress();
//    }
//
//    @Operation(summary = "Cập nhật description cho cái Project Progress của nhóm")
//    @PutMapping("/update/{progressId}")
//    public Response<ProjectProgress> updateProjectProgress(@PathVariable Long progressId, @Valid @RequestBody UpdateProgressRequest updateTopicRequest) {
//        return projectProgressService.updateProjectProgress(progressId, updateTopicRequest);
//    }
//
//    @DeleteMapping("/delete/{progressId}")
//    public Response<Void> deleteProjectProgress(@PathVariable Long progressId) {
//        return projectProgressService.deleteProjectProgress(progressId);
//    }
//
//    // ** PROGRESS COLUMN ** //
//
//    // Create Progress Column base on Project Progress Id
//    @Operation(summary = "Tạo column cho Project Progress ")
//    @PostMapping("/column/create/{projectProgressId}")
//    public Response<ColumnCreateRequestDTO> createColumn(@PathVariable Long projectProgressId) {
//        return projectProgressService.createColumn(projectProgressId);
//    }
//
//    // Update Column
//    @Operation(summary = "Update column")
//    @PutMapping("/column/update/{columnId}")
//    public Response<ColumnUpdateRequestDTO> updateColumn(@PathVariable Long columnId, @RequestBody ColumnUpdateRequestDTO columnUpdateRequestDTO) {
//        return projectProgressService.updateColumn(columnId, columnUpdateRequestDTO);
//    }
//
//    // Delete Column
//    @Operation(summary = "Xoá column")
//    @DeleteMapping("/column/delete/{columnId}")
//    public Response<String> deleteColumn(@PathVariable Long columnId) {
//        return projectProgressService.deleteColumn(columnId);
//    }
//
//    // ** PROGRESS CARD ** //
//
//    // Create Progress Card
//    @Operation(summary = "Tạo card cho column")
//    @PostMapping("card/create/{columnId}")
//    public Response<ProgressCard> createCard(@PathVariable Long columnId) {
//        return projectProgressService.createCard(columnId);
//    }
//
//    // Update Progress Card
//    @Operation(summary = "Update card của column")
//    @PutMapping("/card/update/{cardId}")
//    public Response<ProgressCard> updateCard(@PathVariable Long cardId, @RequestBody UpdateCardRequestDTO updateCardRequestDTO) {
//        return projectProgressService.updateCard(cardId, updateCardRequestDTO);
//    }
//
//    // Delete Progress Card
//    @Operation(summary = "Xoá card của column")
//    @DeleteMapping("/card/delete/{cardId}")
//    public Response<String> deleteCard(@PathVariable Long cardId) {
//        return projectProgressService.deleteCard(cardId);
//    }

    // ** PROGRESS TASK ** //

    // View Group's Progress Tasks
    @Operation(summary = "Xem tất cả các task của nhóm")
    @GetMapping("/my-group/task")
    public Response<ProjectProgressTasksViewDTO> viewGroupTasks() {
        return projectProgressService.viewGroupTasks();
    }

    // Create Progress Task
    @Operation(summary = "Tạo task cho project progress của nhóm")
    @PostMapping("/task/create")
    public Response<ProgressTaskResponseDTO> createTask() {
        return projectProgressService.createTask();
    }

    // Update Progress Task's Assignee
    @Operation(summary = "Update Status cho task")
    @PutMapping("/task/update/status/{taskId}")
    public Response<ProgressTaskResponseDTO> updateTaskStatus(@PathVariable Long taskId, @RequestBody UpdateTaskStatusDTO updateTaskStatusDTO) {
        if (updateTaskStatusDTO.getTaskStatus() != null) {
            return projectProgressService.updateTaskStatus(taskId, updateTaskStatusDTO);
        } else {
            return new Response<>(400, "Please choose a status!", null);
        }
    }

    // Update Progress Task's Assignee
    @Operation(summary = "Update Status cho task")
    @PutMapping("/task/update/summary/{taskId}")
    public Response<ProgressTaskResponseDTO> updateTaskStatus(@PathVariable Long taskId, @RequestBody UpdateSummaryRequestDTO updateSummaryRequestDTO) {
        if (updateSummaryRequestDTO.getSummary() != null) {
            return projectProgressService.updateTaskSummary(taskId, updateSummaryRequestDTO);
        } else {
            return new Response<>(400, "Please input summary!", null);
        }
    }

    // Update Progress Task's Assignee
    @Operation(summary = "Update Assignee cho task")
    @PutMapping("/task/update/assignee/{taskId}")
    public Response<ProgressTaskResponseDTO> updateTaskAssginee(@PathVariable Long taskId, @RequestBody UpdateTaskAssigneeRequestDTO updateTaskAssigneeRequestDTO) {
        if (updateTaskAssigneeRequestDTO.getAssigneeId() != null) {
            return projectProgressService.updateTaskAssginee(taskId, updateTaskAssigneeRequestDTO);
        } else {
            return new Response<>(400, "Please choose a assignee!", null);
        }
    }

    // Update Progress Task's Due Date
    @Operation(summary = "Update Due Date cho task")
    @PutMapping("/task/update/due-date/{taskId}")
    public Response<ProgressTaskResponseDTO> updateTaskDueDate(@PathVariable Long taskId, @RequestBody UpdateTaskDueDateRequestDTO updateTaskDueDateRequestDTO) {
        if (updateTaskDueDateRequestDTO.getDueDate() != null) {
            return projectProgressService.updateTaskDueDate(taskId, updateTaskDueDateRequestDTO);
        } else {
            return new Response<>(400, "Please choose a due date!", null);
        }
    }

    // Update Progress Task's Priority
    @Operation(summary = "Update Priority cho task")
    @PutMapping("/task/update/priority/{taskId}")
    public Response<ProgressTaskResponseDTO> updateTaskPriority(@PathVariable Long taskId, @RequestBody TaskPriorityEnum taskPriorityEnum) {
        return projectProgressService.updateTaskPriority(taskId, taskPriorityEnum);
    }

    // Delete Progress Task
    @Operation(summary = "Xoá task")
    @DeleteMapping("/task/delete/{taskId}")
    public Response<ProgressTaskResponseDTO> deleteTask(@PathVariable Long taskId) {
        return projectProgressService.deleteTask(taskId);
    }

}
