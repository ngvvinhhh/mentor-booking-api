package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.website_feedback.WebsiteFeedbackRequestDTO;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResquest;
import com.swd392.mentorbooking.entity.Enum.BlogCategoryEnum;
import com.swd392.mentorbooking.entity.Enum.WebsiteFeedbackEnum;
import com.swd392.mentorbooking.service.WebsiteFeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/website-feedback")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class WebsiteFeedbackController {
    @Autowired
    WebsiteFeedbackService websiteFeedbackService;

    // Create website feedback
    @PostMapping("/create")
    public Response<WebsiteFeedbackRequestDTO> createWebsiteFeedback(@RequestBody WebsiteFeedbackRequestDTO websiteFeedbackEnum) {
        return websiteFeedbackService.createWebsiteFeedback(websiteFeedbackEnum);
    }

    // Get all feedback type
    @GetMapping("/feedback-type")
    public Response<List<WebsiteFeedbackEnum>> getFeedbackType() {
        List<WebsiteFeedbackEnum> data = new ArrayList<>(Arrays.asList(WebsiteFeedbackEnum.values()));
        return new Response<>(200, "Retrieve feedback types successfully!", data);
    }

    @PutMapping("/update/{feedbackId}")
    public Response<WebsiteFeedbackResponse> updateFeedback(@PathVariable Long feedbackId, @Valid @RequestBody WebsiteFeedbackResquest request) {
        return websiteFeedbackService.updateFeedbackWebsite(feedbackId,request);
    }

    @DeleteMapping("/delete/{feedbackId}")
    public Response<String> deleteSemester(@PathVariable Long feedbackId ){
        return websiteFeedbackService.deleteFeedback(feedbackId);
    }
}
