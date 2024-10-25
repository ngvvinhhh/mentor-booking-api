package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.semester.SemesterRequest;
import com.swd392.mentorbooking.dto.semester.SemesterResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Semester;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.SemesterRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SemesterService {

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    AccountUtils accountUtils;

    public Response<List<SemesterResponse>> getAllSemesters() {
        try {
            // Get all semesters not deleted
            List<Semester> semesters = semesterRepository.findByIsDeletedFalse();

            // Convert Semester list to SemesterResponse
            List<SemesterResponse> semesterResponses = semesters.stream()
                    .map(semester -> new SemesterResponse(semester.getId(), semester.getSemesterName(), semester.getStartDate(), semester.getEndDate()))
                    .collect(Collectors.toList());

            return new Response<>(200, "Retrieved all semesters successfully!", semesterResponses);
        } catch (Exception e) {
            return new Response<>(500, "Failed to retrieve semesters", null);
        }
    }

    public Response<SemesterResponse> createSemester(@Valid SemesterRequest semesterRequest) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Create semester and set fields
        Semester semester = new Semester();
        semester.setSemesterName(semesterRequest.getSemesterName());
        semester.setStartDate(semesterRequest.getStartDate());
        semester.setIsCurrentSemester(true);
        // Add 10 weeks to startDate to set endDate
        semester.setEndDate(semesterRequest.getStartDate().plusWeeks(10));


        semester.setIsDeleted(false);

        // Save semester
        try {
            semesterRepository.save(semester);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the semester, please try again...");
        }

        // Create a response entity
        SemesterResponse semesterResponse = new SemesterResponse(
                semester.getId(),
                semester.getSemesterName(),
                semester.getStartDate(),
                semester.getEndDate()
        );

        return new Response<>(200, "Semester created successfully!", semesterResponse);
    }

    // Update Semester
    public Response<SemesterResponse> updateSemester(Long id, @Valid SemesterRequest semesterRequest) {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Find semester by ID
        Semester semester = semesterRepository.findById(id).orElse(null);
        if (semester == null || semester.getIsDeleted()) {
            return new Response<>(404, "Semester not found or has been deleted", null);
        }

        // Update semester fields
        semester.setSemesterName(semesterRequest.getSemesterName());

        // Save updated semester
        try {
            semesterRepository.save(semester);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the semester, please try again...");
        }

        // Create response
        SemesterResponse semesterResponse = new SemesterResponse(
                semester.getId(),
                semester.getSemesterName(),
                semester.getStartDate(),
                semester.getEndDate()
        );

        return new Response<>(200, "Semester updated successfully!", semesterResponse);
    }

    // Delete Semester
    public Response<String> deleteSemester(Long id) {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Find semester by ID
        Semester semester = semesterRepository.findById(id).orElse(null);
        if (semester == null || semester.getIsDeleted()) {
            return new Response<>(404, "Semester not found or has already been deleted", null);
        }

        // Soft delete by setting isDeleted to true
        semester.setIsDeleted(true);

        // Save semester with updated delete status
        try {
            semesterRepository.save(semester);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when deleting the semester, please try again...");
        }

        return new Response<>(200, "Semester deleted successfully!", null);
    }
}
