package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.projectprogress.ProjectProgressRequest;
import com.swd392.mentorbooking.dto.projectprogress.UpdateProgressRequest;
import com.swd392.mentorbooking.entity.Group;
import com.swd392.mentorbooking.entity.ProjectProgress;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.GroupRepository;
import com.swd392.mentorbooking.repository.ProjectProgressRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectProgressService {

    @Autowired
    ProjectProgressRepository projectProgressRepository;

    @Autowired
    GroupRepository groupRepository;

    public Response<List<ProjectProgress>> getAllProjectProgress() {
        List<ProjectProgress> projectProgressList = projectProgressRepository.findAllByIsDeletedFalse(Sort.by(Sort.Direction.ASC, "createdAt")); // Sort by creation date

        return new Response<>(200, "All Project Progress retrieved successfully!", projectProgressList);
    }

    public Response<ProjectProgress> createProjectProgress (@Valid ProjectProgressRequest projectProgressRequest) {
        Group existingGroup = groupRepository.findById(projectProgressRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + projectProgressRequest.getGroupId()));

        ProjectProgress projectProgress = new ProjectProgress();
        projectProgress.setDescription(projectProgressRequest.getDescription());
        projectProgress.setGroup(existingGroup);
        projectProgress.setCreatedAt(LocalDateTime.now());
        projectProgress.setIsDeleted(false);

        try {
            projectProgressRepository.save(projectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress updated successfully!", projectProgress);

    }

    public Response<ProjectProgress> updateProjectProgress(Long id, @Valid UpdateProgressRequest projectProgressRequest) {
        // Find ProjectProgress by ID
        ProjectProgress existingProjectProgress = projectProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProjectProgress not found with id: " + id));

        // Update
        existingProjectProgress.setDescription(projectProgressRequest.getDescription());

        // Save
        try {
            projectProgressRepository.save(existingProjectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress updated successfully!", existingProjectProgress);
    }

    public Response<Void> deleteProjectProgress(Long id) {

        ProjectProgress existingProjectProgress = projectProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProjectProgress not found with id: " + id));

        existingProjectProgress.setIsDeleted(true);

        try {
            projectProgressRepository.save(existingProjectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when deleting the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress marked as deleted successfully!", null);
    }


}
