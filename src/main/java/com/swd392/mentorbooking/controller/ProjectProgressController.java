package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.projectprogress.*;
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

    @Operation(summary = "Hãy bỏ qua thứ này, chúng ta không đụng")
    @PostMapping("/create")
    public Response<ProjectProgress> createProjectProgress(@Valid @RequestBody ProjectProgressRequest projectProgressRequest){
        return projectProgressService.createProjectProgress(projectProgressRequest);
    }

    @Operation(summary = "Hãy bỏ qua thứ này, chúng ta không đụng")
    @GetMapping("/view")
    public Response<List<ProjectProgress>> getAllProjectProgress(){
        return projectProgressService.getAllProjectProgress();
    }

    @Operation(summary = "Xem project progress của nhóm của thằng đang đăng nhập")
    @GetMapping("/view/my-group")
    public Response<ProjectProgressDTO> getMyProjectProgress(){
        return projectProgressService.getMyProjectProgress();
    }

    @Operation(summary = "Cập nhật description cho cái Project Progress của nhóm")
    @PutMapping("/update/{progressId}")
    public Response<ProjectProgress> updateProjectProgress(@PathVariable Long progressId, @Valid @RequestBody UpdateProgressRequest updateTopicRequest) {
        return projectProgressService.updateProjectProgress(progressId, updateTopicRequest);
    }

    @DeleteMapping("/delete/{progressId}")
    public Response<Void> deleteProjectProgress(@PathVariable Long progressId) {
        return projectProgressService.deleteProjectProgress(progressId);
    }

    // ** PROGRESS COLUMN ** //

    // Create Progress Column base on Project Progress Id
    @Operation(summary = "Tạo column cho Project Progress ")
    @PostMapping("/column/create/{projectProgressId}")
    public Response<ColumnCreateRequestDTO> createColumn(@PathVariable Long projectProgressId) {
        return projectProgressService.createColumn(projectProgressId);
    }

    // Update Column
    @Operation(summary = "Update column")
    @PutMapping("/column/update/{columnId}")
    public Response<ColumnUpdateRequestDTO> updateColumn(@PathVariable Long columnId, @RequestBody ColumnUpdateRequestDTO columnUpdateRequestDTO) {
        return projectProgressService.updateColumn(columnId, columnUpdateRequestDTO);
    }

    // Delete Column
    @Operation(summary = "Xoá column")
    @DeleteMapping("/column/delete/{columnId}")
    public Response<String> deleteColumn(@PathVariable Long columnId) {
        return projectProgressService.deleteColumn(columnId);
    }

    // ** PROGRESS CARD ** //

    // Create Progress Card
    @Operation(summary = "Tạo card cho column")
    @PostMapping("card/create/{columnId}")
    public Response<ProgressCard> createCard(@PathVariable Long columnId) {
        return projectProgressService.createCard(columnId);
    }

    // Update Progress Card
    @Operation(summary = "Update card của column")
    @PutMapping("/card/update/{cardId}")
    public Response<ProgressCard> updateCard(@PathVariable Long cardId, @RequestBody UpdateCardRequestDTO updateCardRequestDTO) {
        return projectProgressService.updateCard(cardId, updateCardRequestDTO);
    }

    // Delete Progress Card
    @Operation(summary = "Xoá card của column")
    @DeleteMapping("/card/delete/{cardId}")
    public Response<String> deleteCard(@PathVariable Long cardId) {
        return projectProgressService.deleteCard(cardId);
    }

}
