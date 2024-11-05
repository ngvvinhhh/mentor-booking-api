package com.swd392.mentorbooking.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("**")
@RequestMapping("/schedule")
@SecurityRequirement(name = "api")
public class ScheduleController {

    // Get mentor's active schedule
    

}
