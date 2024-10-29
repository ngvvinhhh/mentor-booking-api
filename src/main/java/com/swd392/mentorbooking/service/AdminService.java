package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.admin.AccountInfoAdmin;
import com.swd392.mentorbooking.dto.auth.RegisterRequestDTO;
import com.swd392.mentorbooking.dto.auth.RegisterResponseDTO;

import com.swd392.mentorbooking.dto.blog.GetBlogResponseDTO;
import com.swd392.mentorbooking.dto.blog.GetCommentResponseDTO;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Response<List<GetBlogResponseDTO>> viewAllBlogs() {
        // Get data
        List<Blog> allBlogs = blogRepository.findAllByIsDeletedFalse();
        List<GetBlogResponseDTO> data = new ArrayList<>();

        for (Blog blog : allBlogs) {
            GetBlogResponseDTO getBlogResponseDTO = returnOneBlogResponseData(blog);
            data.add(getBlogResponseDTO);
        }

        if (data.isEmpty()) {
            //Response message
            String message = "No blog were found!";
            return new Response<>(200, message, data);
        }

        //Response message
        String message = "Retrieve blogs successfully!";
        return new Response<>(200, message, data);
    }

    private GetBlogResponseDTO returnOneBlogResponseData(Blog blog) {
        List<GetCommentResponseDTO> comments = blog.getComments().stream()
                .filter(comment -> !comment.isDeleted())
                .map(comment -> GetCommentResponseDTO.builder()
                        .id(comment.getId())
                        .authorId(comment.getAccount().getId())
                        .authorName(comment.getAccount().getName())
                        .authorAvatarUrl(comment.getAccount().getAvatar())
                        .description(comment.getDescription())
                        .build())
                .collect(Collectors.toList());

        return GetBlogResponseDTO.builder()
                .id(blog.getId())
                .authorId(blog.getAccount().getId())
                .authorName(blog.getAccount().getName())
                .authorAvatarUrl(blog.getAccount().getAvatar())
                .title(blog.getTitle())
                .image(blog.getImage())
                .category(blog.getBlogCategoryEnum())
                .description(blog.getDescription())
                .likeCount(blog.getLikeCount())
                .createdAt(blog.getCreatedAt())
                .isDeleted(blog.getIsDeleted())
                .comments(comments)
                .build();
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
}
