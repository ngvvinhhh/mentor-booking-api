package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementRequestDTO;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingListResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.mentor.*;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceResponseDTO;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.MentorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/mentor")
@SecurityRequirement(name = "api")

public class MentorController {

    @Autowired
    private MentorService mentorService;

    // ** PROFILE SECTION ** //

    // Update profile's social links
    @PutMapping("/social-update")
    public ResponseEntity<Response<UpdateSocialLinkResponseDTO>> updateSocialLink(@RequestBody UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        Response<UpdateSocialLinkResponseDTO> response = mentorService.updateSocialLink(updateSocialLinkRequestDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ** SERVICE SECTION ** //

    // Create a service - Determine price per hour of mentor
    @PostMapping("/service/create")
    @PreAuthorize("hasAnyAuthority('MENTOR')")
    public Response<CreateServiceResponseDTO> createService(@Valid @RequestBody CreateServiceRequestDTO createServiceRequestDTO) {
        return mentorService.createService(createServiceRequestDTO);
    }

    // Update mentor's service
    @PutMapping("/service/update/{serviceId}")
    public Response<UpdateServiceResponseDTO> updateService(@PathVariable Long serviceId, @Valid @RequestBody UpdateServiceRequestDTO updateServiceRequestDTO) {
        return mentorService.updateService(serviceId, updateServiceRequestDTO);
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

    @PutMapping("/specialization/update")
    public Response<UpdateSpecializationResponseDTO> updateSpecialization(@RequestBody UpdateSpecializationRequestDTO updateSpecializationRequestDTO) {
        return mentorService.updateSpecialization(updateSpecializationRequestDTO);
    }

    // ** ACHIEVEMENT SECTION ** //

    @GetMapping("/achievement/get")
    public Response<List<CreateAchievementResponseDTO>> getAllAchievements() {
        return mentorService.getAllAchievements();
    }

    @PostMapping("/achievement/create")
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

    // ** SCHEDULE SECTION ** //

    // Create schedule
    @PostMapping("/schedule/create")
    public Response<CreateScheduleResponseDTO> createSchedule(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) {
        return mentorService.createSchedule(createScheduleRequestDTO);
    }

    // Delete schedule - This schedule must still be available to delete
    @DeleteMapping("/schedule/delete/{scheduleId}")
    public Response deleteSchedule(@PathVariable Long scheduleId) {
        return mentorService.deleteSchedule(scheduleId);
    }

    // Get Active Schedule
    @GetMapping("/schedule/get/active")
    public Response<List<GetScheduleResponseDTO>> getActiveSchedules() {
        return mentorService.getActiveSchedules();
    }

    // ** BOOKING SECTION ** //

    // Lấy danh sách booking cần dược duyệt
    @GetMapping("/booking/view")
    public Response<List<BookingListResponseDTO>> getAllProcessingBooking() {
        return mentorService.getAllProcessingBooking();
    }

    @GetMapping("/booking/view-upcoming")
    public Response<List<BookingListResponseDTO>> getAllUpcomingBooking() {
        return mentorService.getAllUpcomingBooking();
    }

    @PostMapping("/booking/approve/{bookingId}")
    public Response<BookingResponse> approveBooking(@PathVariable Long bookingId) {
        return mentorService.approveBooking(bookingId);
    }

    @PostMapping("/booking/reject/{bookingId}")
    public Response<BookingResponse> rejectBooking(@PathVariable Long bookingId) {
        return mentorService.rejectBooking(bookingId);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Response<String>> withdrawFunds(@RequestParam Double amount) {
        Response<String> response = mentorService.withdrawFunds(amount);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
    }
}
