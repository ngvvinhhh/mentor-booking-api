package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementRequestDTO;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementResponseDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.CreateBlogRespnseDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogRequestDTO;
import com.swd392.mentorbooking.dto.blog.UpdateBlogResponseDTO;
import com.swd392.mentorbooking.dto.mentor.*;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Achievement;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.entity.Services;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.AchievementRepository;
import com.swd392.mentorbooking.repository.BlogRepository;
import com.swd392.mentorbooking.repository.ServiceRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MentorService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    public Response<UpdateSocialLinkResponseDTO> updateSocialLink(UpdateSocialLinkRequestDTO updateSocialLinkRequestDTO) {
        // Check the current logged in account
        Account account1 = checkAccount();

        // Update account fields if they are not null
        Optional.ofNullable(updateSocialLinkRequestDTO.getYoutubeLink()).ifPresent(account1::setYoutubeLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getFacebookLink()).ifPresent(account1::setFacebookLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getLinkedinLink()).ifPresent(account1::setLinkedinLink);
        Optional.ofNullable(updateSocialLinkRequestDTO.getTwitterLink()).ifPresent(account1::setTwitterLink);

        // Save to database
        accountRepository.save(account1);

        // Create response using updated data
        UpdateSocialLinkResponseDTO responseDTO = new UpdateSocialLinkResponseDTO(account1.getId(), account1.getEmail(), account1.getYoutubeLink(), account1.getFacebookLink(), account1.getLinkedinLink(), account1.getTwitterLink());

        return new Response<>(202, "Update social links successfully!", responseDTO);
    }

    // ** SERVICE SECTION ** //

    public Response<CreateServiceResponseDTO> createService(CreateServiceRequestDTO createServiceRequestDTO) {

        // Check the current logged in account
        Account account = checkAccount();

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
        CreateServiceResponseDTO data = new CreateServiceResponseDTO(service.getPrice(), service.getDescription(), service.getCreatedAt());

        // Return data, message and code
        return new Response<>(201, "Service created successfully!", data);
    }

    public Response<UpdateServiceResponseDTO> updateService(Long serviceId, @Valid UpdateServiceRequestDTO updateServiceRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // Find the service
        Services service = serviceRepository.findById(serviceId).orElse(null);
        if (service == null) return new Response<>(404, "Service not found", null);

        if (!service.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This service does not belong to you to update");
        }

        // Update service fields
        service.setUpdatedAt(LocalDateTime.now());
        if (updateServiceRequestDTO.getPrice() != null) service.setPrice(updateServiceRequestDTO.getPrice());
        if (updateServiceRequestDTO.getDescription() != null)
            service.setDescription(updateServiceRequestDTO.getDescription());

        // Save and handle exceptions
        try {
            serviceRepository.save(service);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the service, please try again...");
        }

        // Return response
        UpdateServiceResponseDTO data = new UpdateServiceResponseDTO(service.getPrice(), service.getDescription(), service.getUpdatedAt());
        return new Response<>(202, "Service updated successfully!", data);
    }

    // ** BLOG SECTION ** //

    public Response<CreateBlogRespnseDTO> createBlog(@Valid CreateBlogRequestDTO createBlogRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // Create blog and set fields
        Blog blog = new Blog();
        blog.setTitle(createBlogRequestDTO.getTitle());
        blog.setDescription(createBlogRequestDTO.getDescription());
        blog.setImage(createBlogRequestDTO.getImage());
        blog.setLikeCount(0);
        blog.setAccount(account);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        blog.setIsDeleted(false);

        // Save blog
        try {
            blogRepository.save(blog);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the blog, please try again...");
        }

        // Create a response entity
        CreateBlogRespnseDTO createBlogRespnseDTO = new CreateBlogRespnseDTO(blog.getTitle(), blog.getDescription(), blog.getImage(), blog.getCreatedAt());

        return new Response<>(201, "Blog created successfully!", createBlogRespnseDTO);
    }

    public Response<UpdateBlogResponseDTO> updateBlog(Long blogId, @Valid UpdateBlogRequestDTO updateBlogRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // Find the service
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) return new Response<>(404, "Service not found", null);
        if (!blog.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This blog does not belong to you to update");
        }

        // Update blog fields
        blog.setUpdatedAt(LocalDateTime.now());
        if (updateBlogRequestDTO.getTitle() != null) blog.setTitle(updateBlogRequestDTO.getTitle());
        if (updateBlogRequestDTO.getDescription() != null) blog.setDescription(updateBlogRequestDTO.getDescription());
        if (updateBlogRequestDTO.getImage() != null) blog.setDescription(updateBlogRequestDTO.getTitle());

        // Save and handle exceptions
        try {
            blogRepository.save(blog);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the blog, please try again...");
        }

        // Return response
        UpdateBlogResponseDTO data = UpdateBlogResponseDTO.builder()
                .title(blog.getTitle())
                .description(blog.getDescription())
                .image(blog.getImage())
                .updatedAt(LocalDateTime.now())
                .build();
        return new Response<>(202, "Service updated successfully!", data);
    }

    public Response<UpdateBlogResponseDTO> deleteBlog(Long blogId) {
        // Check account
        Account account = checkAccount();

        // Get the blog
        Blog blog = blogRepository.findById(blogId).orElse(null);
        if (blog == null) return new Response<>(404, "Blog not found", null);

        if (!blog.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This blog does not belong to you to delete");
        }
        blogRepository.save(blog);

        // Return response
        UpdateBlogResponseDTO data = UpdateBlogResponseDTO.builder()
                .title(blog.getTitle())
                .is_deleted(blog.getIsDeleted())
                .build();

        return new Response<>(202, "Blog deleted successfully", null);
    }

    // ** CV SECTION ** //

    public Response<UploadCVRequest> createCV(@Valid UploadCVRequest uploadCVRequest) {
        // Check the current logged in account
        Account account = checkAccount();

        account.setCv(uploadCVRequest.getFileLink());
        accountRepository.save(account);

        return new Response<>(202, "Upload CV successfully!", uploadCVRequest);
    }

    public Response<UploadCVRequest> updateCV(@Valid UploadCVRequest uploadCVRequest) {
        // Check the current logged in account
        Account account = checkAccount();

        account.setCv(uploadCVRequest.getFileLink());
        accountRepository.save(account);

        return new Response<>(202, "Update CV successfully!", uploadCVRequest);
    }

    public Response<UploadCVRequest> deleteCV() {
        Account account = checkAccount();
        account.setCv(null);
        accountRepository.save(account);
        return new Response<>(202, "CV deleted successfully!", null);
    }

    // ** SPECIALIZATION SECTION ** //

    public Response<List<SpecializationEnum>> getAllAvailableSpecialization() {
        // Check account
        Account account = checkAccount();

        List<SpecializationEnum> enumList = new ArrayList<>(Arrays.asList(SpecializationEnum.values()));
        List<SpecializationEnum> existingSpecializations = account.getSpecializations();

        // Remove existing specializations from enumList
        enumList.removeAll(existingSpecializations);
        return new Response<>(200, "Retrieve specialization successfully", enumList);
    }

    public Response<UpdateSpecializationResponseDTO> updateSpecialization(UpdateSpecializationRequestDTO updateSpecializationRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        // If mentor have no specialization yet.
        if (account.getSpecializations().isEmpty()) {
            account.setSpecializations(updateSpecializationRequestDTO.getEnumList());
        }

        // Else add to the list
        else {
            for (SpecializationEnum specializationEnum : updateSpecializationRequestDTO.getEnumList()) {
                if (!account.getSpecializations().contains(specializationEnum)) {
                    account.getSpecializations().add(specializationEnum);
                }
            }
        }
        accountRepository.save(account);

        UpdateSpecializationResponseDTO response = new UpdateSpecializationResponseDTO();
        response.setSpecializationList(account.getSpecializations());
        response.setName(account.getName());
        response.setEmail(account.getEmail());

        return new Response<>(202, "Specialization updated successfully!", response);
    }

    // ** ACHIEVEMENT SECTION ** //

    public Response<List<CreateAchievementResponseDTO>> getAllAchievements() {
        // Check the current logged in account
        Account account = checkAccount();

        // Get all achievements for the current account
        List<Achievement> achievements = achievementRepository.findAllByAccountId(account.getId());

        // Convert achievements to CreateAchievementResponseDTO
        List<CreateAchievementResponseDTO> responseDTOs = achievements.stream().map(achievement ->
                CreateAchievementResponseDTO.builder()
                        .achievementName(achievement.getAchievementName())
                        .achievementLink(achievement.getLink())
                        .achievementDescription(achievement.getDescription())
                        .created_at(achievement.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());

        // Return the response
        return new Response<>(200, "Retrieve all achievements successfully!", responseDTOs);
    }


    public Response<CreateAchievementResponseDTO> createAchievement(@Valid CreateAchievementRequestDTO createAchievementRequestDTO) {
        // Check the current logged in account
        Account account = checkAccount();

        Achievement achievement = Achievement.builder().achievementName(createAchievementRequestDTO.getAchievementName()).link(createAchievementRequestDTO.getAchievementLink()).description(createAchievementRequestDTO.getAchievementDescription()).createdAt(LocalDateTime.now()).account(account).isDeleted(false).build();

        achievementRepository.save(achievement);

        CreateAchievementResponseDTO response = CreateAchievementResponseDTO.builder().achievementName(createAchievementRequestDTO.getAchievementName()).achievementLink(createAchievementRequestDTO.getAchievementLink()).achievementDescription(createAchievementRequestDTO.getAchievementDescription()).created_at(achievement.getCreatedAt()).build();

        return new Response<>(201, "Create achievement successfully!", response);
    }

    public Response<CreateAchievementResponseDTO> updateAchievement(Long achievementId, @Valid CreateAchievementRequestDTO createAchievementRequestDTO) {

        // Check the current logged in account
        Account account = checkAccount();

        // Get the current achievement
        Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
        if (achievement == null) {
            return new Response<>(404, "Achievement not found", null);
        }

        if (!achievement.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This achievement does not belong to you to update");
        }

        // Set the updated time
        achievement.setUpdatedAt(LocalDateTime.now());

        // Save new achievement info
        achievementRepository.save(achievement);

        CreateAchievementResponseDTO response = CreateAchievementResponseDTO.builder().achievementName(createAchievementRequestDTO.getAchievementName()).achievementLink(createAchievementRequestDTO.getAchievementLink()).achievementDescription(createAchievementRequestDTO.getAchievementDescription()).update_at(achievement.getUpdatedAt()).build();

        return new Response<>(202, "Update achievement successfully!", response);
    }

    public Response<CreateAchievementResponseDTO> deleteAchievement(Long achievementId) {
        // Get the current account
        Account account = checkAccount();

        // Get the current achievement
        Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
        if (achievement == null) {
            return new Response<>(404, "Achievement not found", null);
        }

        if (!achievement.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This achievement does not belong to you to delete");
        }

        achievement.setIsDeleted(true);
        achievementRepository.save(achievement);

        CreateAchievementResponseDTO response = CreateAchievementResponseDTO.builder().achievementName(achievement.getAchievementName()).achievementLink(achievement.getLink()).build();

        return new Response<>(202, "Deleted achievement: " + achievement.getAchievementName(), response);

    }


    private Account checkAccount() {
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
