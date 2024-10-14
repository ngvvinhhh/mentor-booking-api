package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.SearchMentorResponseDTO;
import com.swd392.mentorbooking.dto.account.student.GetProfileResponse;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.WebsiteFeedback;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.WebsiteFeedbackRepository;
import com.swd392.mentorbooking.utils.AccountSpecification;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WebsiteFeedbackRepository websiteFeedbackRepository;

    public Response<GetProfileResponse> getProfile() {
        // Get the current account
        Account account = checkAccount();

        GetProfileResponse response = GetProfileResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole())
                .dayOfBirth(account.getDayOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .avatar(account.getAvatar())
                .className(account.getClassName())
                .specializations(account.getSpecializations())
                .facebookLink(account.getFacebookLink())
                .linkedinLink(account.getLinkedinLink())
                .twitterLink(account.getTwitterLink())
                .youtubeLink(account.getYoutubeLink())
                .build();
        return new Response<>(200, "Retrieve data successfully", response);
    }


    public Response<GetProfileResponse> getProfileById(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        GetProfileResponse response = GetProfileResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole())
                .dayOfBirth(account.getDayOfBirth())
                .gender(account.getGender())
                .phone(account.getPhone())
                .avatar(account.getAvatar())
                .className(account.getClassName())
                .specializations(account.getSpecializations())
                .facebookLink(account.getFacebookLink())
                .linkedinLink(account.getLinkedinLink())
                .twitterLink(account.getTwitterLink())
                .youtubeLink(account.getYoutubeLink())
                .build();
        return new Response<>(200, "Retrieve data successfully", response);
    }

    public Response<List<SearchMentorResponseDTO>> searchMentor(String name, Double minPrice, Double maxPrice, List<SpecializationEnum> specializations, Sort sort) {

        Specification<Account> specification = Specification.where(AccountSpecification.hasName(name))
                .and(AccountSpecification.hasRole(RoleEnum.MENTOR))
                .and(AccountSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(AccountSpecification.hasAllSpecializations(specializations));

        // Fetch all accounts that match the specification and sort them
        List<Account> accounts = accountRepository.findAll(specification, sort);

        List<SearchMentorResponseDTO> returnData = accounts.stream()
                .map(this::convertToSearchMentorResponseDTO)
                .collect(Collectors.toList());

        // Fetch the accounts and map them to AccountDTO
        return new Response<>(200, "Retrieve data successfully!", returnData);
    }

    private SearchMentorResponseDTO convertToSearchMentorResponseDTO(Account account) {
        SearchMentorResponseDTO searchMentorResponseDTO = new SearchMentorResponseDTO();
        searchMentorResponseDTO.setAccountId(account.getId());
        searchMentorResponseDTO.setAccountName(account.getName());
        searchMentorResponseDTO.setAccountEmail(account.getEmail());
        searchMentorResponseDTO.setAccountPhone(account.getPhone());
        searchMentorResponseDTO.setPricePerHour(account.getService().getPrice());
        searchMentorResponseDTO.setEnumList(account.getSpecializations());
        searchMentorResponseDTO.setAvatar(account.getAvatar());
        return searchMentorResponseDTO;
    }

    public Response<String> createWebsiteFeedback(String description) {
        Account account = checkAccount();

        WebsiteFeedback websiteFeedback = WebsiteFeedback.builder()
                .account(account)
                .description(description)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        websiteFeedbackRepository.save(websiteFeedback);

        return new Response<>(201, "Feedback created successfully!", description);
    }

    public Account checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        return account;
    }

}

