package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.servicefeedback.ServicesFeedbackRequest;
import com.swd392.mentorbooking.dto.servicefeedback.ServicesFeedbackResponse;
import com.swd392.mentorbooking.dto.servicefeedback.UpdateServiceFeedbackResquest;
import com.swd392.mentorbooking.dto.servicefeedback.UpdateServicesFeedbackResponse;
import com.swd392.mentorbooking.service.FeedbackServicessService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-feedback")
@CrossOrigin("**")
public class ServicesFeedbackController {
    @Autowired
    private FeedbackServicessService serviceFeedbackService;

    @GetMapping("/all")
    public Response<List<ServicesFeedbackResponse>> getAllFeedbacks() {
        return serviceFeedbackService.getAllActiveFeedbacks();
    }


    @PostMapping("create")
    public Response<ServicesFeedbackResponse> createFeedback(@Valid @RequestBody ServicesFeedbackRequest request) {
        return serviceFeedbackService.createFeedback(request);
    }

    @PutMapping("/update/{feedbackId}")
    public Response<UpdateServicesFeedbackResponse> updateFeedback(@PathVariable Long feedbackId, @Valid @RequestBody UpdateServiceFeedbackResquest request) {
        return serviceFeedbackService.updateFeedback(feedbackId,request);
    }

    @DeleteMapping("/delete/{feedbackId}")
    public Response<String> deleteFeedback(@PathVariable Long feedbackId ){
        return serviceFeedbackService.deleteFeedback(feedbackId);
    }

}
