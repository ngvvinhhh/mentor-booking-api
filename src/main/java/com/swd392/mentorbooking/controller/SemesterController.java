package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.semester.SemesterRequest;
import com.swd392.mentorbooking.dto.semester.SemesterResponse;
import com.swd392.mentorbooking.dto.topic.TopicRequest;
import com.swd392.mentorbooking.dto.topic.TopicResponse;
import com.swd392.mentorbooking.service.SemesterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/semester")
@CrossOrigin("**")
public class SemesterController {

    @Autowired
    SemesterService semesterService;

    @PostMapping("create")
    public Response<SemesterResponse> createTopic(@Valid @RequestBody SemesterRequest semesterRequest){
        return semesterService.createSemester(semesterRequest);
    }

}
