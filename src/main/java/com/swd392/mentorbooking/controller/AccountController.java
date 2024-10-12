package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.mentor.GetMentorProfileResponse;
import com.swd392.mentorbooking.dto.account.student.GetStudentProfileResponse;
import com.swd392.mentorbooking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("**")
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/student/profile")
    public Response<GetStudentProfileResponse> getStudentProfile() {
        return accountService.getStudentProfile();
    }

    @GetMapping("/mentor/profile")
    public Response<GetMentorProfileResponse> getMentorProfile() {
        return accountService.getMentorProfile();
    }

}
