package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.mentor.GetMentorProfileResponse;
import com.swd392.mentorbooking.dto.account.student.GetStudentProfileResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;


    public Response<GetStudentProfileResponse> getStudentProfile() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        account = accountRepository.findById(account.getId()).orElse(null);
        if (account == null) return new Response<>(401, "Account not found", null);

        //Get account avatar

        GetStudentProfileResponse response = GetStudentProfileResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .dayOfBirth(account.getDayOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .avatar(account.getAvatar())
                .className(account.getClassName())
                .build();
        return new Response<>(200, "Retrieve data successfully", response);
    }

    public Response<GetMentorProfileResponse> getMentorProfile() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        account = accountRepository.findById(account.getId()).orElse(null);
        if (account == null) return new Response<>(401, "Account not found", null);

        GetMentorProfileResponse response = GetMentorProfileResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .dayOfBirth(account.getDayOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .avatar(account.getAvatar())
                .specializations(account.getSpecializations())
                .facebookLink(account.getFacebookLink())
                .linkedinLink(account.getLinkedinLink())
                .twitterLink(account.getTwitterLink())
                .youtubeLink(account.getYoutubeLink())
                .build();
        return new Response<>(200, "Retrieve data successfully", response);
    }
}
