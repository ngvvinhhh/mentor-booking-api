package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.semester.SemesterRequest;
import com.swd392.mentorbooking.dto.semester.SemesterResponse;
import com.swd392.mentorbooking.service.SemesterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semester")
@CrossOrigin("**")
public class SemesterController {

    @Autowired
    SemesterService semesterService;

    // Get All Semesters
    @GetMapping("/all")
    public Response<List<SemesterResponse>> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    @PostMapping("create")
    public Response<SemesterResponse> createTopic(@Valid @RequestBody SemesterRequest semesterRequest){
        return semesterService.createSemester(semesterRequest);
    }

    // Update Semester
    @PutMapping("/update/{semesterId}")
    public Response<SemesterResponse> updateSemester(@PathVariable Long semesterId, @Valid @RequestBody SemesterRequest semesterRequest) {
        return semesterService.updateSemester(semesterId, semesterRequest);
    }

    // Delete Semester (Soft Delete)
    @DeleteMapping("/delete/{semesterId}")
    public Response<String> deleteSemester(@PathVariable Long semesterId ){
        return semesterService.deleteSemester(semesterId);
    }

}
