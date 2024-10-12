package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.SearchMentorResponseDTO;
import com.swd392.mentorbooking.dto.account.mentor.GetMentorProfileResponse;
import com.swd392.mentorbooking.dto.account.student.GetStudentProfileResponse;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // ** PROFILE SECTION ** //

    @GetMapping("/student/profile")
    public Response<GetStudentProfileResponse> getStudentProfile() {
        return accountService.getStudentProfile();
    }

    @GetMapping("/mentor/profile")
    public Response<GetMentorProfileResponse> getMentorProfile() {
        return accountService.getMentorProfile();
    }

    // ** SEARCH SECTION ** //

    @GetMapping("/search-mentor")
    public Response<List<SearchMentorResponseDTO>> searchMentor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<SpecializationEnum> specializations,
            @RequestParam(defaultValue = "service.price,asc") String[] sort
    )
    {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sortCriteria = Sort.by(direction, sort[0]);

        return accountService.searchMentor(name, minPrice, maxPrice, specializations, sortCriteria);
    }

    // ** WEBSITE FEEDBACK SECTION ** //

    // Create website feedback
    @PostMapping("/website/create-feedback")
    public Response<String> createWebsiteFeedback(@RequestBody String description) {
        return accountService.createWebsiteFeedback(description);
    }

}
