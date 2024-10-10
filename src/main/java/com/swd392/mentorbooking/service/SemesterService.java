package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.semester.SemesterRequest;
import com.swd392.mentorbooking.dto.semester.SemesterResponse;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Semester;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.SemesterRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SemesterService {

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    AccountUtils accountUtils;
    public Response<SemesterResponse> createSemester(@Valid SemesterRequest semesterRequest) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        // Create semester and set fields
        Semester semester = new Semester();
        semester.setTopicName(semesterRequest.getTopicName());

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
                semester.getTopicName()
        );

        return new Response<>(200, "Semester created successfully!", semesterResponse);
    }
}
