package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResquest;
import com.swd392.mentorbooking.service.WebsiteFeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/website-feedback")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class WebsiteFeedbackController {
    @Autowired
    WebsiteFeedbackService websiteFeedbackService;

    @GetMapping("/all")
    public Response<List<WebsiteFeedbackResponse>> getAllFeedbackWebsite() {
        return websiteFeedbackService.getAllFeedbackWebsite();
    }


    @PostMapping("create")
    public Response<WebsiteFeedbackResponse> createFeedback(@Valid @RequestBody WebsiteFeedbackResquest request) {
        return websiteFeedbackService.createFeedbackWebsite(request);
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
