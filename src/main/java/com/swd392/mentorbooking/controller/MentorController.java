package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogResponseDTO;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkRequestDTO;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkResponseDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceResponseDTO;
import com.swd392.mentorbooking.service.MentorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("**")
@RequestMapping("/mentor")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    // Update profile's social links
    @PostMapping("/social-update")
    public ResponseEntity<Response<UpdateSocialLinkResponseDTO>> updateSocialLink(
            @RequestBody UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        Response<UpdateSocialLinkResponseDTO> response = mentorService.updateSocialLink(updateSocialLinkRequestDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

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

    // Create a blog
    @PostMapping("/blog/create")
    public Response<CreateBlogRespnseDTO> createBlog(@Valid @RequestBody CreateBlogRequestDTO createBlogRequestDTO) {
        return mentorService.createBlog(createBlogRequestDTO);
    }

    @PutMapping("/blog/update/{blogId}")
    public Response<UpdateBlogResponseDTO> updateService(@PathVariable Long blogId, @Valid @RequestBody UpdateBlogRequestDTO updateServiceRequestDTO) {
        return mentorService.updateBlog(blogId, updateServiceRequestDTO);
    }
}
