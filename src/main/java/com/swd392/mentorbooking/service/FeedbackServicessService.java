package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.servicefeedback.ServicesFeedbackRequest;
import com.swd392.mentorbooking.dto.servicefeedback.ServicesFeedbackResponse;
import com.swd392.mentorbooking.dto.servicefeedback.UpdateServiceFeedbackResquest;
import com.swd392.mentorbooking.dto.servicefeedback.UpdateServicesFeedbackResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.ServiceFeedback;
import com.swd392.mentorbooking.entity.Services;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.ServiceFeedbackRepository;
import com.swd392.mentorbooking.repository.ServiceRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServicessService {

    @Autowired
    ServiceFeedbackRepository serviceFeedbackRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    AccountUtils accountUtils;

    public Response<List<ServicesFeedbackResponse>> getAllActiveFeedbacks() {

        // Fetch all active (non-deleted) feedbacks
        List<ServiceFeedback> activeFeedbacks = serviceFeedbackRepository.findAllActiveFeedbacks();

        // Map each feedback to ServicesFeedbackResponse
        List<ServicesFeedbackResponse> feedbackResponses = activeFeedbacks.stream()
                .map(feedback -> new ServicesFeedbackResponse(
                        feedback.getId(),
                        feedback.getService().getId(),
                        feedback.getRating(),
                        feedback.getDescription(),
                        feedback.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new Response<>(200, "Fetched all active feedbacks successfully!", feedbackResponses);
    }

    public Response<ServicesFeedbackResponse> createFeedback(@Valid ServicesFeedbackRequest request) {

        // Check for Service existence
        Services existingService = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new NotFoundException("Service not found with id: " + request.getServiceId()));

        // Check for Account existence
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        // Create new feedback
        ServiceFeedback serviceFeedback = new ServiceFeedback();
        serviceFeedback.setService(existingService);
        serviceFeedback.setAccount(account);
        serviceFeedback.setRating(request.getRating());
        serviceFeedback.setDescription(request.getDescription());
        serviceFeedback.setCreatedAt(LocalDateTime.now());
        serviceFeedback.setIsDeleted(false);

        // Save feedback
        serviceFeedbackRepository.save(serviceFeedback);

        // Create a response entity
        ServicesFeedbackResponse feedbackResponse = new ServicesFeedbackResponse(
                serviceFeedback.getId(),
                serviceFeedback.getService().getId(),
                serviceFeedback.getRating(),
                serviceFeedback.getDescription(),
                serviceFeedback.getCreatedAt()
        );

        return new Response<>(200, "Feedback created successfully!", feedbackResponse);
    }

    public Response<UpdateServicesFeedbackResponse> updateFeedback(Long feedbackId, @Valid UpdateServiceFeedbackResquest request) {

        // Check for feedback existence
        ServiceFeedback existingFeedback = serviceFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found with id: " + feedbackId));

        // Check for Account existence
        Account account = accountUtils.getCurrentAccount();
        if (!existingFeedback.getAccount().getId().equals(account.getId())) {
            throw new AuthAppException(ErrorCode.ACCOUNT_ACCESS_FORBIDDEN); // Ensure the user is the one who created the feedback
        }

        // Update feedback details
        if (request.getRating() != null) {
            existingFeedback.setRating(request.getRating());
        }
        if (request.getDescription() != null) {
            existingFeedback.setDescription(request.getDescription());
        }
        existingFeedback.setUpdatedAt(LocalDateTime.now());

        // Save updated feedback
        serviceFeedbackRepository.save(existingFeedback);

        UpdateServicesFeedbackResponse feedbackResponse = new UpdateServicesFeedbackResponse(
                existingFeedback.getId(),
                existingFeedback.getService().getId(),
                existingFeedback.getRating(),
                existingFeedback.getDescription(),
                existingFeedback.getUpdatedAt()
        );

        return new Response<>(200, "Feedback updated successfully!", feedbackResponse);
    }

    public Response<String> deleteFeedback(Long feedbackId) {

        // Check for feedback existence
        ServiceFeedback existingFeedback = serviceFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found with id: " + feedbackId));

        // Check if feedback is already deleted
        if (existingFeedback.getIsDeleted()) {
            return new Response<>(400, "Feedback already deleted!", null);
        }

        // Check for Account existence
        Account account = accountUtils.getCurrentAccount();
        if (!existingFeedback.getAccount().getId().equals(account.getId())) {
            throw new AuthAppException(ErrorCode.ACCOUNT_ACCESS_FORBIDDEN); // Ensure the user is the one who created the feedback
        }

        // Mark feedback as deleted
        existingFeedback.setIsDeleted(true);

        // Save the feedback with deletedAt updated
        serviceFeedbackRepository.save(existingFeedback);

        return new Response<>(200, "Feedback deleted successfully!", null);
    }



}
