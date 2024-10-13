package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementRequestDTO;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementResponseDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogResponseDTO;
import com.swd392.mentorbooking.dto.mentor.*;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceResponseDTO;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.MentorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/mentor")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    // ** PROFILE SECTION ** //

    // Update profile's social links
    @PostMapping("/social-update")
    public ResponseEntity<Response<UpdateSocialLinkResponseDTO>> updateSocialLink(@RequestBody UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        Response<UpdateSocialLinkResponseDTO> response = mentorService.updateSocialLink(updateSocialLinkRequestDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ** SERVICE SECTION ** //

    // Create a service - Determine price per hour of mentor
    @PostMapping("/service/create")
    public Response<CreateServiceResponseDTO> createService(@Valid @RequestBody CreateServiceRequestDTO createServiceRequestDTO) {
        return mentorService.createService(createServiceRequestDTO);
    }

    // Update mentor's service
    @PutMapping("/service/update/{serviceId}")
    public Response<UpdateServiceResponseDTO> updateService(@PathVariable Long serviceId, @Valid @RequestBody UpdateServiceRequestDTO updateServiceRequestDTO) {
        return mentorService.updateService(serviceId, updateServiceRequestDTO);
    }

    // ** BLOG SECTION ** //

    // Create a blog
    @PostMapping("/blog/create")
    public Response<CreateBlogRespnseDTO> createBlog(@Valid @RequestBody CreateBlogRequestDTO createBlogRequestDTO) {
        return mentorService.createBlog(createBlogRequestDTO);
    }

    // Update a blog
    @PutMapping("/blog/update/{blogId}")
    public Response<UpdateBlogResponseDTO> updateService(@PathVariable Long blogId, @Valid @RequestBody UpdateBlogRequestDTO updateServiceRequestDTO) {
        return mentorService.updateBlog(blogId, updateServiceRequestDTO);
    }

    @DeleteMapping("/blog/delete/{blogId}")
    public Response<UpdateBlogResponseDTO> deleteBlog(@PathVariable Long blogId) {
        return mentorService.deleteBlog(blogId);
    }

    // ** CV SECTION ** //

    // Create a CV
    @PostMapping("/cv/create")
    public Response<UploadCVRequest> createCV(@Valid @RequestBody UploadCVRequest uploadCVRequest) {
        return mentorService.createCV(uploadCVRequest);
    }

    // Update a CV
    @PutMapping("/cv/update")
    public Response<UploadCVRequest> updateCV(@Valid @RequestBody UploadCVRequest uploadCVRequest) {
        return mentorService.updateCV(uploadCVRequest);
    }

    // Delete a CV
    @DeleteMapping("/cv/delete")
    public Response<UploadCVRequest> deleteCV() {
        return mentorService.deleteCV();
    }
    // ** SPECIALIZATION SECTION ** //

    @GetMapping("/specialization/get")
    public Response<List<SpecializationEnum>> getAllSpecialization() {
        return mentorService.getAllAvailableSpecialization();
    }

    @PostMapping("/specialization/update")
    public Response<UpdateSpecializationResponseDTO> updateSpecialization(@RequestBody UpdateSpecializationRequestDTO updateSpecializationRequestDTO) {
        return mentorService.updateSpecialization(updateSpecializationRequestDTO);
    }

    // ** ACHIEVEMENT SECTION ** //

    @GetMapping("/achievement/get")
    public Response<List<CreateAchievementResponseDTO>> getAllAchievements() {
        return mentorService.getAllAchievements();
    }

    @GetMapping("/achievement/create")
    public Response<CreateAchievementResponseDTO> createAchievement(@Valid @RequestBody CreateAchievementRequestDTO createAchievementRequestDTO) {
        return mentorService.createAchievement(createAchievementRequestDTO);
    }

    @PutMapping("/achievement/update/{achievementId}")
    public Response<CreateAchievementResponseDTO> updateAchievement(@PathVariable Long achievementId, @Valid @RequestBody CreateAchievementRequestDTO createAchievementRequestDTO) {
        return mentorService.updateAchievement(achievementId, createAchievementRequestDTO);
    }

    @DeleteMapping("/achievement/delete/{achievementId}")
    public Response<CreateAchievementResponseDTO> deleteAchievement(@PathVariable Long achievementId) {
        return mentorService.deleteAchievement(achievementId);
    }
}
