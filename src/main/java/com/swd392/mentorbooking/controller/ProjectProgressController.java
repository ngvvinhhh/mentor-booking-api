package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.projectprogress.ProjectProgressRequest;
import com.swd392.mentorbooking.dto.projectprogress.UpdateProgressRequest;
import com.swd392.mentorbooking.entity.ProjectProgress;
import com.swd392.mentorbooking.service.ProjectProgressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@CrossOrigin("**")
public class ProjectProgressController {
    @Autowired
    ProjectProgressService projectProgressService;

    @PostMapping("create")
    public Response<ProjectProgress> createProjectProgress(@Valid @RequestBody ProjectProgressRequest projectProgressRequest){
        return projectProgressService.createProjectProgress(projectProgressRequest);
    }

    @GetMapping("view")
    public Response<List<ProjectProgress>> getAllProjectProgresss(){
        return projectProgressService.getAllProjectProgress();
    }

    @PutMapping("/update/{progressId}")
    public Response<ProjectProgress> updateProjectProgress(@PathVariable Long progressId, @Valid @RequestBody UpdateProgressRequest updateTopicRequest) {
        return projectProgressService.updateProjectProgress(progressId, updateTopicRequest);
    }

    @DeleteMapping("/delete/{progressId}")
    public Response<Void> deleteProjectProgress(@PathVariable Long progressId) {
        return projectProgressService.deleteProjectProgress(progressId);
    }

}
