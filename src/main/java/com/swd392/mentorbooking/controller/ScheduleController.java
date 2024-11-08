package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.mentor.GetScheduleResponseDTO;
import com.swd392.mentorbooking.service.ScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/schedule")
@SecurityRequirement(name = "api")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // Get mentor's active schedule
    @GetMapping("/mentor/{mentorId}")
    public Response<List<GetScheduleResponseDTO>> getMentorActiveSchedules(@PathVariable long mentorId) {
        return scheduleService.getMentorActiveSchedules(mentorId);
    }

}
