package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.SearchMentorResponseDTO;
import com.swd392.mentorbooking.dto.account.GetProfileResponse;
import com.swd392.mentorbooking.dto.account.UpdateProfileRequestDTO;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin("**")
@RequestMapping("/account")
@SecurityRequirement(name = "api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // ** PROFILE SECTION ** //

    @GetMapping("/profile")
    public Response<GetProfileResponse> getProfile() {
        return accountService.getProfile();
    }

    @PutMapping("/profile/update-profile")
    public Response<GetProfileResponse> UpdateProfile(@Valid @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO) {
        return accountService.updateProfile(updateProfileRequestDTO);
    }

    @GetMapping("profile/{accountId}")
    public Response<GetProfileResponse> getProfileById(@PathVariable long accountId) {
        return accountService.getProfileById(accountId);
    }

    // ** SEARCH SECTION ** //

    @GetMapping("/search-mentor")
    public Response<List<SearchMentorResponseDTO>> searchMentor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") Double minPrice,
            @RequestParam(required = false, defaultValue = "999999") Double maxPrice,
            @RequestParam(required = false) List<SpecializationEnum> specializations,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "service.price,asc") String[] sort
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sortCriteria = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortCriteria);

        // Gọi dịch vụ tìm kiếm và lấy Page
        List<SearchMentorResponseDTO> mentors = accountService.searchMentor(name, minPrice, maxPrice, specializations, pageable);

        // Trả về Response với danh sách mentor
        return new Response<>(200, "Retrieve data successfully!", mentors);
    }

    @GetMapping("/specialization/get-all")
    public Response<List<SpecializationEnum>> getSpecializations() {
        return new Response<>(200, "Retrieve data successfully!", Arrays.stream(SpecializationEnum.values()).toList());
    }
}
