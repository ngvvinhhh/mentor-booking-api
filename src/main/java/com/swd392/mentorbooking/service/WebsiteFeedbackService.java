package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResquest;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.WebsiteFeedback;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.WebsiteFeedbackRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebsiteFeedbackService {

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    WebsiteFeedbackRepository websiteFeedbackRepository;

    public Response<List<WebsiteFeedbackResponse>> getAllFeedbackWebsite() {
        // Lấy tất cả phản hồi không bị xóa (isDeleted = false)
        List<WebsiteFeedback> feedbackList = websiteFeedbackRepository.findByIsDeletedFalse();

        // Chuyển đổi danh sách phản hồi thành danh sách WebsiteFeedbackResponse
        List<WebsiteFeedbackResponse> feedbackResponses = feedbackList.stream()
                .map(feedback -> new WebsiteFeedbackResponse(
                        feedback.getId(),
                        feedback.getDescription(),
                        feedback.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new Response<>(200, "Successfully fetched all feedbacks!", feedbackResponses);
    }

    public Response<WebsiteFeedbackResponse> createFeedbackWebsite(@Valid WebsiteFeedbackResquest request) {

        // Check for Account existence
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        // Create new feedback
        WebsiteFeedback websiteFeedback = new WebsiteFeedback();
        websiteFeedback.setAccount(account);
        websiteFeedback.setDescription(request.getDescription());
        websiteFeedback.setCreatedAt(LocalDateTime.now());
        websiteFeedback.setIsDeleted(false);

        // Save feedback
        websiteFeedbackRepository.save(websiteFeedback);

        // Create a response entity
        WebsiteFeedbackResponse feedbackResponse = new WebsiteFeedbackResponse(
                websiteFeedback.getId(),
                websiteFeedback.getDescription(),
                websiteFeedback.getCreatedAt()
        );

        return new Response<>(200, "Feedback created successfully!", feedbackResponse);
    }

    public Response<WebsiteFeedbackResponse> updateFeedbackWebsite(Long feedbackId, @Valid WebsiteFeedbackResquest request) {

        // Check for feedback existence
        WebsiteFeedback existingFeedback = websiteFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotFoundException("Feedback not found with id: " + feedbackId));

        // Check for Account existence
        Account account = accountUtils.getCurrentAccount();
        if (!existingFeedback.getAccount().getId().equals(account.getId())) {
            throw new AuthAppException(ErrorCode.ACCOUNT_ACCESS_FORBIDDEN); // Ensure the user is the one who created the feedback
        }

        // Update feedback details
        if (request.getDescription() != null) {
            existingFeedback.setDescription(request.getDescription());
        }
        existingFeedback.setUpdatedAt(LocalDateTime.now());

        // Save updated feedback
        websiteFeedbackRepository.save(existingFeedback);

        WebsiteFeedbackResponse feedbackResponse = new WebsiteFeedbackResponse(
                existingFeedback.getId(),
                existingFeedback.getDescription(),
                existingFeedback.getUpdatedAt()
        );

        return new Response<>(200, "Feedback updated successfully!", feedbackResponse);
    }

    public Response<String> deleteFeedback(Long feedbackId) {

        // Check for feedback existence
        WebsiteFeedback existingFeedback = websiteFeedbackRepository.findById(feedbackId)
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
        websiteFeedbackRepository.save(existingFeedback);

        return new Response<>(200, "Feedback deleted successfully!", null);
    }
}
