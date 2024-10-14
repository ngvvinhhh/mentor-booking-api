package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.SearchMentorResponseDTO;
import com.swd392.mentorbooking.dto.account.student.GetProfileResponse;
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

    @GetMapping("/profile")
    public Response<GetProfileResponse> getProfile() {
        return accountService.getProfile();
    }

    @GetMapping("/profile/{accountId}")
    public Response<GetProfileResponse> getProfileById(@PathVariable("accountId") Long accountId) {
        return accountService.getProfileById(accountId);
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
