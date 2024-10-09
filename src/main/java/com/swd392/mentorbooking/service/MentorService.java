package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkRequestDTO;
import com.swd392.mentorbooking.dto.mentor.UpdateSocialLinkResponseDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Services;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.ServiceRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MentorService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private ServiceRepository serviceRepository;

    public Response<UpdateSocialLinkResponseDTO> updateSocialLink(UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first", null);
        }

        // Find account by email
        Account account1 = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account1 == null) {
            return new Response<>(404, "Account not found", null);
        }

        // Update account fields if they are not null
        Optional.ofNullable(updateSocialLinkRequestDTO.getYoutubeLink()).ifPresent(account1::setYoutubeLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getFacebookLink()).ifPresent(account1::setFacebookLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getLinkedinLink()).ifPresent(account1::setLinkedinLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getTwitterLink()).ifPresent(account1::setTwitterLink);

        // Save to database
        accountRepository.save(account1);

        // Create response using updated data
        UpdateSocialLinkResponseDTO responseDTO = new UpdateSocialLinkResponseDTO(
                account1.getId(),
                account1.getEmail(),
                account1.getYoutubeLink(),
                account1.getFacebookLink(),
                account1.getLinkedinLink(),
                account1.getTwitterLink()
        );

        return new Response<>(200, "Update social links successfully!", responseDTO);
    }

    public Response<CreateServiceResponseDTO> createService(CreateServiceRequestDTO createServiceRequestDTO) {

        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first", null);
        }

        Services service = new Services();
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        service.setIsDeleted(false);
        service.setPrice(createServiceRequestDTO.getPrice());
        service.setDescription(createServiceRequestDTO.getDescription());
        service.setAccount(account);
        try {
            serviceRepository.save(service);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the service, please try again...");
        }

        // Create response data
        CreateServiceResponseDTO data = new CreateServiceResponseDTO(
                service.getPrice(),
                service.getDescription(),
                service.getCreatedAt()
        );

        // Return data, message and code
        return new Response<>(200, "Service created successfully!", data);
    }
}
