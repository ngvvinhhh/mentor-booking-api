package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.website_feedback.WebsiteFeedbackRequestDTO;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResquest;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.WebsiteFeedback;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.AccountRepository;
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

    @Autowired
    private AccountRepository accountRepository;

    public Response<WebsiteFeedbackRequestDTO> createWebsiteFeedback(WebsiteFeedbackRequestDTO websiteFeedbackRequestDTO) {
        Account account = checkAccount();

        WebsiteFeedback websiteFeedback = WebsiteFeedback.builder()
                .account(account)
                .description(websiteFeedbackRequestDTO.getDescription())
                .websiteFeedbackEnum(websiteFeedbackRequestDTO.getWebsiteFeedbackEnum())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        websiteFeedbackRepository.save(websiteFeedback);

        return new Response<>(201, "Feedback created successfully!", websiteFeedbackRequestDTO);
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

    public Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return account;
    }
}
