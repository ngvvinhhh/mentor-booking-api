package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.admin.AccountInfoAdmin;
import com.swd392.mentorbooking.dto.auth.RegisterRequestDTO;
import com.swd392.mentorbooking.dto.auth.RegisterResponseDTO;

import com.swd392.mentorbooking.dto.booking.BookingResponse;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.BookingStatus;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Enum.WalletLogType;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WebsiteFeedbackRepository websiteFeedbackRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WalletLogRepository walletLogRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public Response<List<AccountInfoAdmin>> getAllAccountByRole(String role) {

        List<Account> data;

        try {
            if (role == null) {
                // Get all accounts
                data = accountRepository.findAccountsByIsDeletedFalse();
            } else if (role.equalsIgnoreCase("mentor")) {
                // Get accounts by mentor role
                data = accountRepository.findAccountsByRoleAndIsDeletedFalse(RoleEnum.MENTOR);
            } else if (role.equalsIgnoreCase("student")) {
                // Get accounts by student role
                data = accountRepository.findAccountsByRoleAndIsDeletedFalse(RoleEnum.STUDENT);
            } else {
                // Role not supported
                String message = "Your role is not supported!";
                return new Response<>(400, message, null); // Return 400 Bad Request
            }

            // Check if data is not null or empty
            if (data == null || data.isEmpty()) {
                String message = "No data found!";
                return new Response<>(404, message, null); // Return 404 Not Found
            }

            // Convert list of Account to list of AccountInfoAdmin
            List<AccountInfoAdmin> returnData = data.stream()
                    .map(AccountInfoAdmin::fromAccount)
                    .collect(Collectors.toList());

            // Return response
            String message = "Retrieve data successfully!";
            return new Response<>(200, message, returnData);

        } catch (Exception e) {
            // Log the exception
            String message = "Error occurred while processing request!";
            return new Response<>(500, message, null); // Return 500 Internal Server Error
        }
    }

    public Response<List<Topic>> getAllTopic() {
        // Get data
        List<Topic> data = topicRepository.findAll();

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);
    }

    public Response<List<Blog>> getAllBlog() {
        // Get data
        List<Blog> data = blogRepository.findAll();

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);
    }

    public Response<List<Booking>> getAllBooking() {
        // Get data
        List<Booking> data = bookingRepository.findAll();

        //Response message
        String message = "Retrieve topics successfully!";
        return new Response<>(200, message, data);
    }

    public Response<List<WebsiteFeedbackResponse>> getAllFeedbackWebsite() {
        // Lấy tất cả phản hồi không bị xóa (isDeleted = false)
        List<WebsiteFeedback> feedbackList = websiteFeedbackRepository.findAll();

        // Chuyển đổi danh sách phản hồi thành danh sách WebsiteFeedbackResponse
        List<WebsiteFeedbackResponse> feedbackResponses = feedbackList.stream()
                .map(feedback -> new WebsiteFeedbackResponse(
                        feedback.getId(),
                        feedback.getDescription(),
                        feedback.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new Response<>(200, "Successfully fetched all feedbacks!", feedbackResponses);
    }

    public Response<AccountInfoAdmin> deleteAccount(Long accountId) {
        //checkAccount();

        Account accountToDelete = accountRepository.findById(accountId).orElse(null);
        if (accountToDelete == null) {
            return new Response<>(400, "Account not found!", null);
        }

        accountToDelete.setIsDeleted(true);
        accountRepository.save(accountToDelete);
        return new Response<>(202, "Account successfully deleted!", null);
    }

    public void checkAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            throw new AuthAppException(ErrorCode.NOT_LOGIN);
        }

        account = accountRepository.findByEmail(account.getEmail()).orElse(null);
        if (account == null) {
            throw new AuthAppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        if (!account.getRole().equals(RoleEnum.ADMIN)) {
            throw new ForbiddenException("You are not allowed to perform admin's action!");
        }

    }

    public ResponseEntity<RegisterResponseDTO> addNewAccount(RegisterRequestDTO registerRequestDTO) {
        try {
            //Check if the email exist
            Account tempAccount = accountRepository.findByEmail(registerRequestDTO.getEmail()).orElse(null);
            if (tempAccount != null) {
                if (tempAccount.getStatus().equals(AccountStatusEnum.VERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_EXISTED);
                } else if (tempAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_WAIT_VERIFY);
                }
            }
            Account account = new Account();
            account.setName(registerRequestDTO.getName());
            account.setEmail(registerRequestDTO.getEmail());
            account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
            account.setRole(registerRequestDTO.getRole());
            account.setStatus(AccountStatusEnum.VERIFIED);
            account.setCreatedAt(LocalDateTime.now());
            account.setIsDeleted(false);
            account.setAvatar("https://firebasestorage.googleapis.com/v0/b/mentor-booking-3d46a.appspot.com/o/76f15d2d-9f0b-4051-8177-812d5ee785a1.jpg?alt=media");
            accountRepository.save(account);

            //Create wallet as the account is created here
            Wallet wallet = Wallet.builder()
                    .account(account)
                    .total(0.0)
                    .build();
            walletRepository.save(wallet);

            String responseMessage = "Email created successfully";
            RegisterResponseDTO response = new RegisterResponseDTO(responseMessage, null, 201, registerRequestDTO.getEmail());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorMessage = "Register failed";
            RegisterResponseDTO response = new RegisterResponseDTO(errorMessage, errorCode.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }


    }

    @Transactional
    public Response<BookingResponse> completeBooking(Long bookingId) {
        // Check admin account
        Account adminAccount = accountUtils.getCurrentAccount();
        if (adminAccount == null || !adminAccount.getRole().equals(RoleEnum.ADMIN)) {
            return new Response<>(401, "Access denied. Only admin can complete bookings.", null);
        }

        // Find booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Check booking status
        if (!booking.getStatus().equals(BookingStatus.SUCCESSFUL)) {
            return new Response<>(400, "Booking cannot be completed as it is not in successful status.", null);
        }

        // Find mentor accounts and their wallets
        Account mentorAccount = booking.getSchedule().getAccount();
        Wallet walletMentor = walletRepository.findByAccount(mentorAccount);
        if (walletMentor == null) {
            throw new NotFoundException("Mentor's wallet not found!!");
        }

        // Get admin wallet
        Wallet walletAdmin = walletRepository.findByAccount(adminAccount);
        if (walletAdmin == null) {
            throw new NotFoundException("Admin's wallet not found!!");
        }

        Services services = serviceRepository.findByAccount(mentorAccount);
        if (services == null) {
            throw new NotFoundException("Service not found!!");
        }

        // Transaction log for mentor
        WalletLog walletLogMentor = new WalletLog();
        walletLogMentor.setWallet(walletMentor);
        walletLogMentor.setAmount(services.getPrice() * 95 / 100);
        walletLogMentor.setFrom(adminAccount.getId());
        walletLogMentor.setTo(mentorAccount.getId());
        walletLogMentor.setTypeOfLog(WalletLogType.TRANSFER);
        walletLogMentor.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLogMentor);

        walletAdmin.setTotal(walletAdmin.getTotal() - (services.getPrice() * 95 / 100));

        // Transaction log for admin
        WalletLog walletLogAdmin = new WalletLog();
        walletLogAdmin.setWallet(walletAdmin);
        walletLogAdmin.setAmount(services.getPrice() * 95 / 100);
        walletLogAdmin.setFrom(adminAccount.getId());
        walletLogAdmin.setTo(mentorAccount.getId());
        walletLogAdmin.setTypeOfLog(WalletLogType.TRANSFER);
        walletLogAdmin.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(walletLogAdmin);

        walletMentor.setTotal(walletMentor.getTotal() + (services.getPrice() * 95 / 100));

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

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
                .scheduleId(booking.getSchedule().getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(booking.getSchedule().getAccount().getName())
                .build();

        return new Response<>(200, "Booking completed successfully!", bookingResponse);
    }

    @Transactional
    public Response<BookingResponse> cancelBooking(Long bookingId) {
        // Check admin account
        Account adminAccount = accountUtils.getCurrentAccount();
        if (adminAccount == null || !adminAccount.getRole().equals(RoleEnum.ADMIN)) {
            return new Response<>(401, "Access denied. Only admin can cancel bookings.", null);
        }

        // Find booking by ID
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        // Check booking status
        if (!booking.getStatus().equals(BookingStatus.SUCCESSFUL)) {
            return new Response<>(400, "Booking cannot be canceled as it is not in successful status.", null);
        }

        // Get student's wallet
        Wallet walletStudent = walletRepository.findByAccount(booking.getAccount());
        if (walletStudent == null) {
            throw new NotFoundException("Student's wallet not found!!");
        }

        Account admin = accountRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));

        Wallet walletAdmin = walletRepository.findByAccount(admin);
        if (walletAdmin == null) {
            throw new NotFoundException("Admin's wallet not found!!");
        }

        Services services = serviceRepository.findByAccount(booking.getSchedule().getAccount());
        if (services == null) {
            throw new NotFoundException("Service not found!!");
        }

        // Log refund for students
        WalletLog refundLog = new WalletLog();
        refundLog.setWallet(walletStudent);
        refundLog.setAmount(services.getPrice() * 95 / 100);
        refundLog.setFrom(admin.getId());
        refundLog.setTo(booking.getAccount().getId());
        refundLog.setTypeOfLog(WalletLogType.TRANSFER);
        refundLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(refundLog);

        walletStudent.setTotal(walletStudent.getTotal() + (services.getPrice() * 95 / 100));

        // Transaction log for admin
        WalletLog adminLog = new WalletLog();
        adminLog.setWallet(walletAdmin);
        adminLog.setAmount(services.getPrice() * 95 / 100);
        adminLog.setFrom(admin.getId());
        adminLog.setTo(booking.getAccount().getId());
        adminLog.setTypeOfLog(WalletLogType.TRANSFER);
        adminLog.setCreatedAt(LocalDateTime.now());
        walletLogRepository.save(adminLog);

        walletAdmin.setTotal(walletAdmin.getTotal() - (services.getPrice() * 95 / 100));

        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Notification notification = notificationRepository.findByBookingAndAccount(booking, booking.getAccount())
                .orElse(new Notification());

        notification.setMessage(booking.getStatus().getMessage());
        notification.setStatus(booking.getStatus());

        notificationRepository.save(notification);

        // Tạo đối tượng phản hồi
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getBookingId())
                .location(booking.getLocation())
                .locationNote(booking.getLocationNote())
                .scheduleId(booking.getSchedule().getId())
                .message(notification.getMessage())
                .status(booking.getStatus())
                .mentorName(booking.getSchedule().getAccount().getName())
                .build();

        return new Response<>(200, "Booking canceled successfully!", bookingResponse);
    }
}
