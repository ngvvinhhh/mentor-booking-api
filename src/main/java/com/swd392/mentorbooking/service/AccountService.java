package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.account.UpdateProfileRequestDTO;
import com.swd392.mentorbooking.dto.achievement.GetAchievementResponseDTO;
import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.SpecializationEnum;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.account.SearchMentorResponseDTO;
import com.swd392.mentorbooking.dto.account.GetProfileResponse;
import com.swd392.mentorbooking.entity.Enum.WalletLogType;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountSpecification;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WebsiteFeedbackRepository websiteFeedbackRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WalletLogRepository walletLogRepository;



    // ** PROFILE SECTION ** //

    public Response<GetProfileResponse> getProfile() {
        // Get the current account
        Account account = checkAccount();

        // Return the result
        return returnProfile(account);
    }

    public Response<GetProfileResponse> getProfileById(long accountId) {
        Account data = accountRepository.findById(accountId).orElse(null);

        if (data != null) {
            return returnProfile(data);
        } else {
            return new Response<>(200, "No profile was found!", null);
        }
    }

    public Response<GetProfileResponse> updateProfile(@Valid UpdateProfileRequestDTO updateProfileRequestDTO) {
        Account account = checkAccount();

        // Check if field is null
        Optional.ofNullable(updateProfileRequestDTO.getName()).ifPresent(account::setName);
        Optional.ofNullable(updateProfileRequestDTO.getEmail()).ifPresent(account::setEmail);
        Optional.ofNullable(updateProfileRequestDTO.getPhone()).ifPresent(account::setPhone);
        Optional.ofNullable(updateProfileRequestDTO.getDayOfBirth()).ifPresent(account::setDayOfBirth);
        Optional.ofNullable(updateProfileRequestDTO.getGender()).ifPresent(account::setGender);
        Optional.ofNullable(updateProfileRequestDTO.getAvatar()).ifPresent(account::setAvatar);
        Optional.ofNullable(updateProfileRequestDTO.getClassName()).ifPresent(account::setClassName);

        // Chỉ cập nhật className nếu người dùng là student
        if (account.getRole() == RoleEnum.STUDENT) {
            Optional.ofNullable(updateProfileRequestDTO.getClassName()).ifPresent(account::setClassName);
        }
        // Chỉ cập nhật các link nếu người dùng là mentor
        if (account.getRole() == RoleEnum.MENTOR) {
            Optional.ofNullable(updateProfileRequestDTO.getFacebookLink()).ifPresent(account::setFacebookLink);
            Optional.ofNullable(updateProfileRequestDTO.getYoutubeLink()).ifPresent(account::setYoutubeLink);
            Optional.ofNullable(updateProfileRequestDTO.getTwitterLink()).ifPresent(account::setTwitterLink);
            Optional.ofNullable(updateProfileRequestDTO.getLinkedinLink()).ifPresent(account::setLinkedinLink);
        }

        // Save info
        accountRepository.save(account);

        return returnProfile(account);
    }

    private Response<GetProfileResponse> returnProfile(Account account) {
        // Get user wallet
        Wallet wallet = walletRepository.findByAccount(account);

        Services services = serviceRepository.findByAccount(account);

        List<GetAchievementResponseDTO> achievementList = new ArrayList<>();

        if (!account.getAchievements().isEmpty()) {
            for (Achievement achievement : account.getAchievements()) {
                GetAchievementResponseDTO achievementResponseDTO = GetAchievementResponseDTO.builder()
                        .id(achievement.getId())
                        .achievementName(achievement.getAchievementName())
                        .link(achievement.getLink())
                        .description(achievement.getDescription())
                        .build();
                achievementList.add(achievementResponseDTO);
            }
        }

        GetProfileResponse response = null;
        if (services != null) {
            response = GetProfileResponse.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .email(account.getEmail())
                    .role(account.getRole())
                    .dayOfBirth(account.getDayOfBirth())
                    .gender(account.getGender())
                    .phone(account.getPhone())
                    .avatar(account.getAvatar())
                    .className(account.getClassName())
                    // ** wallet ** //
                    .walletId(wallet.getId())
                    .walletPoint(wallet.getTotal())
                    // ** mentor ** //
                    .serviceId(services.getId())
                    .servicePrice(services.getPrice())
                    .specializations(account.getSpecializations())
                    .achievements(achievementList)
                    .facebookLink(account.getFacebookLink())
                    .linkedinLink(account.getLinkedinLink())
                    .twitterLink(account.getTwitterLink())
                    .youtubeLink(account.getYoutubeLink())
                    .build();
        }
        else {
            response = GetProfileResponse.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .email(account.getEmail())
                    .role(account.getRole())
                    .dayOfBirth(account.getDayOfBirth())
                    .gender(account.getGender())
                    .phone(account.getPhone())
                    .avatar(account.getAvatar())
                    .className(account.getClassName())
                    // ** wallet ** //
                    .walletId(wallet.getId())
                    .walletPoint(wallet.getTotal())
                    .build();
        }
        return new Response<>(200, "Retrieve data successfully", response);
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

    // ** SEARCH SECTION ** //

    public List<SearchMentorResponseDTO> searchMentor(String name, Double minPrice, Double maxPrice, List<SpecializationEnum> specializations, Pageable pageable) {
        Specification<Account> specification = Specification.where(AccountSpecification.hasName(name))
                .and(AccountSpecification.hasRole(RoleEnum.MENTOR))
                .and(AccountSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(AccountSpecification.hasAllSpecializations(specializations));

        // Fetch all accounts that match the specification with pagination
        Page<Account> accountPage = accountRepository.findAll(specification, pageable);

        // Chuyển đổi danh sách tài khoản thành danh sách DTO
        return accountPage.getContent().stream()
                .map(this::convertToSearchMentorResponseDTO)
                .collect(Collectors.toList());
    }

    private SearchMentorResponseDTO convertToSearchMentorResponseDTO(Account account) {
        SearchMentorResponseDTO searchMentorResponseDTO = new SearchMentorResponseDTO();
        searchMentorResponseDTO.setAccountId(account.getId());
        searchMentorResponseDTO.setAccountName(account.getName());
        searchMentorResponseDTO.setAccountEmail(account.getEmail());
        searchMentorResponseDTO.setAccountPhone(account.getPhone());
        searchMentorResponseDTO.setPricePerHour(account.getService().getPrice());
        searchMentorResponseDTO.setSpecializationList(account.getSpecializations());
        searchMentorResponseDTO.setAvatar(account.getAvatar());
        return searchMentorResponseDTO;
    }

    @Transactional
    public Response<BookingResponse> cancelBooking(Long bookingId) {
        // Check student account
        Account studentAccount = accountUtils.getCurrentAccount();
        if (studentAccount == null) {
            return new Response<>(401, "Access denied. Only students can cancel their own bookings.", null);
        }

        // Find booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Check booking status
        if (!booking.getStatus().equals(BookingStatus.SUCCESSFUL)) {
            return new Response<>(400, "Booking cannot be canceled as it is not in successful status.", null);
        }

        // Get student's wallet
        Wallet walletStudent = walletRepository.findByAccount(studentAccount);
        if (walletStudent == null) {
            throw new NotFoundException("Student's wallet not found!");
        }

        Account admin = accountRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));

        Wallet walletAdmin = walletRepository.findByAccount(admin);
        if (walletAdmin == null) {
            throw new NotFoundException("Admin's wallet not found!");
        }

        Services services = serviceRepository.findByAccount(booking.getSchedule().getAccount());
        if (services == null) {
            throw new NotFoundException("Service not found!");
        }

        // Log refund for students
        WalletLog refundLog = new WalletLog();
        refundLog.setWallet(walletStudent);
        refundLog.setAmount(booking.getTotal() * 95 / 100);
        refundLog.setFrom(admin.getId());
        refundLog.setTo(booking.getGroup().getStudents().getFirst().getId());
        refundLog.setTypeOfLog(WalletLogType.TRANSFER);
        refundLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(refundLog);

        walletStudent.setTotal(walletStudent.getTotal() + (booking.getTotal() * 95 / 100));

        // Transaction log for admin
        WalletLog adminLog = new WalletLog();
        adminLog.setWallet(walletAdmin);
        adminLog.setAmount(booking.getTotal() * 95 / 100);
        adminLog.setFrom(admin.getId());
        adminLog.setTo(booking.getGroup().getStudents().getFirst().getId());
        adminLog.setTypeOfLog(WalletLogType.TRANSFER);
        adminLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(adminLog);

        walletAdmin.setTotal(walletAdmin.getTotal() - (booking.getTotal() * 95 / 100));

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Notification notification = notificationRepository.findByBookingAndAccount(booking, studentAccount)
                .orElse(new Notification());

        notification.setMessage(booking.getStatus().getMessage());
        notification.setStatus(booking.getStatus());

        notificationRepository.save(notification);

        // Tạo đối tượng phản hồi
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .total(booking.getTotal())
                .scheduleId(booking.getSchedule().getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(booking.getSchedule().getAccount().getName())
                .build();

        return new Response<>(200, "Booking canceled successfully!", bookingResponse);
    }

    @Transactional
    public Response<BookingResponse> completeBooking(Long bookingId) {
        // Check student account
        Account studentAccount = accountUtils.getCurrentAccount();
        if (studentAccount == null) {
            return new Response<>(401, "Access denied. Only students can complete their own bookings.", null);
        }

        // Find booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Check booking status
        if (!booking.getStatus().equals(BookingStatus.SUCCESSFUL)) {
            return new Response<>(400, "Booking cannot be completed as it is not in successful status.", null);
        }

        // Find mentor account and wallet
        Account mentorAccount = booking.getSchedule().getAccount();
        Wallet walletMentor = walletRepository.findByAccount(mentorAccount);
        if (walletMentor == null) {
            throw new NotFoundException("Mentor's wallet not found!");
        }

        // Find admin account and wallet
        Account admin = accountRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));
        Wallet walletAdmin = walletRepository.findByAccount(admin);
        if (walletAdmin == null) {
            throw new NotFoundException("Admin's wallet not found!");
        }

        Services services = serviceRepository.findByAccount(mentorAccount);
        if (services == null) {
            throw new NotFoundException("Service not found!");
        }

        // Log payment to mentor
        WalletLog mentorLog = new WalletLog();
        mentorLog.setWallet(walletMentor);
        mentorLog.setAmount(booking.getTotal() * 95 / 100);
        mentorLog.setFrom(admin.getId());
        mentorLog.setTo(mentorAccount.getId());
        mentorLog.setTypeOfLog(WalletLogType.TRANSFER);
        mentorLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(mentorLog);

        walletMentor.setTotal(walletMentor.getTotal() + (booking.getTotal() * 95 / 100));

        // Log transaction for admin
        WalletLog adminLog = new WalletLog();
        adminLog.setWallet(walletAdmin);
        adminLog.setAmount(booking.getTotal() * 95 / 100);
        adminLog.setFrom(admin.getId());
        adminLog.setTo(mentorAccount.getId());
        adminLog.setTypeOfLog(WalletLogType.TRANSFER);
        adminLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(adminLog);

        walletAdmin.setTotal(walletAdmin.getTotal() - (booking.getTotal() * 95 / 100));

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Create or update notification
        Notification notification = notificationRepository.findByBookingAndAccount(booking, studentAccount)
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
                .mentorName(mentorAccount.getName())
                .build();

        return new Response<>(200, "Booking completed successfully!", bookingResponse);
    }

}

