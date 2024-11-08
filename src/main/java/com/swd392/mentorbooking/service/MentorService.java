package com.swd392.mentorbooking.service;


import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementRequestDTO;
import com.swd392.mentorbooking.dto.achievement.CreateAchievementResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingGroupResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingListResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingMentorResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.mentor.*;
import com.swd392.mentorbooking.dto.service.CreateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.CreateServiceResponseDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceRequestDTO;
import com.swd392.mentorbooking.dto.service.UpdateServiceResponseDTO;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.*;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletLogRepository walletLogRepository;

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

        if (account.getService() != null) {
            return new Response<>(200, "You have already had a service!", null);
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

        // Get the current list of specializations
        List<SpecializationEnum> currentSpecializations = account.getSpecializations();

        // If mentor has no specialization yet, set the new list
        if (currentSpecializations.isEmpty()) {
            account.setSpecializations(new ArrayList<>(updateSpecializationRequestDTO.getEnumList()));
        } else {
            // Update the list: add new specializations and remove those not in the new list
            Set<SpecializationEnum> newSpecializations = new HashSet<>(updateSpecializationRequestDTO.getEnumList());

            // Add specializations that are in newSpecializations but not in currentSpecializations
            for (SpecializationEnum specializationEnum : newSpecializations) {
                if (!currentSpecializations.contains(specializationEnum)) {
                    currentSpecializations.add(specializationEnum);
                }
            }

            // Remove specializations that are in currentSpecializations but not in newSpecializations
            currentSpecializations.removeIf(specializationEnum -> !newSpecializations.contains(specializationEnum));
        }

        // Save the updated account
        accountRepository.save(account);

        // Prepare the response
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
        List<Achievement> achievements = achievementRepository.findAllByAccountAndIsDeletedFalse(account);

        // Convert achievements to CreateAchievementResponseDTO
        List<CreateAchievementResponseDTO> responseDTOs = achievements.stream().map(achievement ->
                CreateAchievementResponseDTO.builder()
                        .achievementId(achievement.getId())
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

    // ** SCHEDULE SECTION ** //

    public Response<CreateScheduleResponseDTO> createSchedule(CreateScheduleRequestDTO createScheduleRequestDTO) {
        Account account = checkAccount();

        Schedule schedule = Schedule.builder()
                .account(account)
                .date(createScheduleRequestDTO.getDate())
                .startTime(createScheduleRequestDTO.getStartFrom())
                .endTime(createScheduleRequestDTO.getEndAt())
                .status(ScheduleStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        scheduleRepository.save(schedule);

        CreateScheduleResponseDTO response = CreateScheduleResponseDTO.builder()
                .accountId(account.getId())
                .accountName(account.getName())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .status(schedule.getStatus())
                .build();

        return new Response<>(201, "Created schedule!", response);
    }

    public Response deleteSchedule(Long scheduleId) {
        Account account = checkAccount();

        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            return new Response<>(204, "There is no such schedule with that scheduleId!", null);
        }
        if (!schedule.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("This schedule does not belong to you to delete");
        }
        schedule.setIsDeleted(true);
        scheduleRepository.save(schedule);
        return new Response<>(200, "Schedule with scheduleId " + scheduleId + " is deleted!", true);
    }

    public Response<List<GetScheduleResponseDTO>> getActiveSchedules() {
        Account account = checkAccount();
        List<Schedule> listSchedule = scheduleRepository.findAllByAccountAndStatusAndIsDeletedFalse(account, ScheduleStatus.ACTIVE)
                .orElseGet(Collections::emptyList);

        if (listSchedule.isEmpty()) {
            return new Response<>(204, "There is no active schedules", null);
        }

        List<GetScheduleResponseDTO> data = listSchedule.stream()
                .map(schedule -> GetScheduleResponseDTO.builder()
                        .scheduleId(schedule.getId())
                        .date(schedule.getDate())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .status(schedule.getStatus())
                        .createdAt(schedule.getCreatedAt())
                        .isDeleted(schedule.getIsDeleted())
                        .build())
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieved data successfully!", data);
    }

    // ** BOOKING SECTION ** //

    public Response<List<BookingListResponseDTO>> getAllProcessingBooking() {
        Account account = checkAccount();

        List<BookingListResponseDTO> response = bookingRepository.findBookingsByAccountAndIsDeletedFalse(account)
                .stream()
                .map(booking -> {
                    List<BookingGroupResponseDTO> groupResponseDTO = booking.getGroup().getStudents().stream()
                            .map(account1 -> BookingGroupResponseDTO.builder()
                                    .accountId(account1.getId())
                                    .name(account1.getName())
                                    .email(account1.getEmail())
                                    .build())
                            .collect(Collectors.toList());

                    return BookingListResponseDTO.builder()
                            .bookingId(booking.getBookingId())
                            .location(booking.getLocation())
                            .note(booking.getLocationNote())
                            .bookingDate(booking.getSchedule().getDate())
                            .startTime(booking.getSchedule().getStartTime())
                            .endTime(booking.getSchedule().getEndTime())
                            .mentor(new BookingMentorResponseDTO(account.getId(), account.getName()))
                            .group(groupResponseDTO)
                            .status(booking.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieve booking list successfully!", response);
    }

    @Transactional
    public Response<BookingResponse> approveBooking(Long bookingId) {
        Account mentorAccount = accountUtils.getCurrentAccount();

        if (mentorAccount == null) return new Response<>(401, "Please login first", null);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        Long scheduleId = booking.getSchedule().getId();
        System.out.println("Schedule ID: " + scheduleId);
        Schedule schedule = scheduleRepository.findById(booking.getSchedule().getId())
                .orElseThrow(() -> new NotFoundException("Schedule not found for the provided account and booking"));


        if (!booking.getStatus().equals(BookingStatus.PROCESSING)) {
            return new Response<>(400, "Booking cannot be approved as it is not in processing status.", null);
        }

        Wallet wallet = walletRepository.findByAccount(booking.getGroup().getStudents().getFirst());
        if (wallet == null) {
            throw new NotFoundException("Wallet not found!!");
        }

        Account admin = accountRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));

        Wallet walletAdmin = walletRepository.findByAccount(admin);
        if (walletAdmin == null) {
            throw new NotFoundException("Wallet admin not found!!");
        }

        Services services = serviceRepository.findByAccount(mentorAccount);
        if (services == null) {
            throw new NotFoundException("Service not found!!");
        }

        //Wallet log for student
        WalletLog walletLogStudent = new WalletLog();
        walletLogStudent.setWallet(wallet);
        walletLogStudent.setAmount(booking.getTotal());
        walletLogStudent.setFrom(booking.getGroup().getStudents().getFirst().getId());
        walletLogStudent.setTo(admin.getId());
        walletLogStudent.setTypeOfLog(WalletLogType.TRANSFER);
        walletLogStudent.setCreatedAt(LocalDateTime.now());
            walletLogRepository.save(walletLogStudent);

        wallet.setTotal(wallet.getTotal() - booking.getTotal());

        //Wallet log for admin
        WalletLog walletLogAdmin = new WalletLog();
        walletLogAdmin.setWallet(walletAdmin);
        walletLogAdmin.setAmount(booking.getTotal());
        walletLogAdmin.setFrom(booking.getGroup().getStudents().getFirst().getId());
        walletLogAdmin.setTo(admin.getId());
        walletLogAdmin.setTypeOfLog(WalletLogType.TRANSFER);
        walletLogAdmin.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLogAdmin);

        walletAdmin.setTotal(walletAdmin.getTotal() + booking.getTotal());


        booking.setStatus(BookingStatus.SUCCESSFUL);
        schedule.setStatus(ScheduleStatus.INACTIVE);
        bookingRepository.save(booking);
        scheduleRepository.save(schedule);

        Notification notification = notificationRepository.findByBookingAndAccount(booking, mentorAccount)
                .orElse(new Notification());

        notification.setMessage(booking.getStatus().getMessage());
        notification.setStatus(booking.getStatus());

        notificationRepository.save(notification);

        // Create response object
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .total(booking.getTotal())
                .scheduleId(booking.getSchedule().getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(booking.getSchedule().getAccount().getName()) // Mentor name
                .build();

        return new Response<>(200, "Booking approved successfully!", bookingResponse);
    }

    @Transactional
    public Response<BookingResponse> rejectBooking(Long bookingId) {
        // Get current account (mentor)
        Account mentorAccount = accountUtils.getCurrentAccount();
        if (mentorAccount == null) return new Response<>(401, "Please login first", null);

        // Find the booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Check if the booking is currently in PROCESSING status
        if (!booking.getStatus().equals(BookingStatus.PROCESSING)) {
            return new Response<>(400, "Booking cannot be rejected as it is not in processing status.", null);
        }

        // Update booking status to DECLINED
        booking.setStatus(BookingStatus.DECLINED);
        bookingRepository.save(booking);

        Schedule schedule = scheduleRepository.findById(booking.getSchedule().getId()).orElse(null);
        schedule.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(schedule);

        Notification notification = notificationRepository.findByBookingAndAccount(booking, mentorAccount)
                .orElse(new Notification());

        notification.setMessage(booking.getStatus().getMessage());
        notification.setStatus(booking.getStatus());

        notificationRepository.save(notification);

        // Create response object
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .total(booking.getTotal())
                .scheduleId(booking.getSchedule().getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(booking.getSchedule().getAccount().getName()) // Mentor name
                .build();

        return new Response<>(200, "Booking rejected successfully!", bookingResponse);
    }

    @Transactional
    public Response<String> withdrawFunds(Double amount) {
        Account mentorAccount = accountUtils.getCurrentAccount();
        if (mentorAccount == null) return new Response<>(401, "Please login first", null);

        Wallet wallet = walletRepository.findByAccount(mentorAccount);
        if (wallet == null) {
            return new Response<>(404, "Wallet not found", null);
        }

        if (wallet.getTotal() < amount) {
            return new Response<>(400, "Insufficient funds", null);
        }

        // Reduce the amount in the wallet
        wallet.setTotal(wallet.getTotal() - amount);
        walletRepository.save(wallet);

        // Transaction Logging
        WalletLog walletLog = new WalletLog();
        walletLog.setWallet(wallet);
        walletLog.setAmount(amount);
        walletLog.setFrom(mentorAccount.getId());
        walletLog.setTo(null);
        walletLog.setTypeOfLog(WalletLogType.WITHDRAWAL);
        walletLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLog);

        return new Response<>(200, "Withdrawal successful", "You have withdrawn: " + amount);
    }

    public Response<List<BookingListResponseDTO>> getAllUpcomingBooking() {
        Account account = checkAccount();

        List<BookingListResponseDTO> response = bookingRepository.findBookingsByAccountAndStatusAndIsDeletedFalse(account, BookingStatus.SUCCESSFUL)
                .stream()
                .map(booking -> {
                    List<BookingGroupResponseDTO> groupResponseDTO = booking.getGroup().getStudents().stream()
                            .map(account1 -> BookingGroupResponseDTO.builder()
                                    .accountId(account1.getId())
                                    .name(account1.getName())
                                    .email(account1.getEmail())
                                    .build())
                            .collect(Collectors.toList());

                    return BookingListResponseDTO.builder()
                            .bookingId(booking.getBookingId())
                            .location(booking.getLocation())
                            .note(booking.getLocationNote())
                            .bookingDate(booking.getSchedule().getDate())
                            .startTime(booking.getSchedule().getStartTime())
                            .endTime(booking.getSchedule().getEndTime())
                            .mentor(new BookingMentorResponseDTO(account.getId(), account.getName()))
                            .group(groupResponseDTO)
                            .status(booking.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return new Response<>(200, "Retrieve booking list successfully!", response);
    }
}
