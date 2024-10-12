package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.admin.AccountInfoAdmin;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.ForbiddenException;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.BlogRepository;
import com.swd392.mentorbooking.repository.BookingRepository;
import com.swd392.mentorbooking.repository.TopicRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Response<List<AccountInfoAdmin>> getAllAccountByRole(String role) {

        List<Account> data;

        try {
            if (role == null) {
                // Get all accounts
                data = accountRepository.findAll();
            } else if (role.equalsIgnoreCase("mentor")) {
                // Get accounts by mentor role
                data = accountRepository.findAccountsByRole(RoleEnum.MENTOR);
            } else if (role.equalsIgnoreCase("student")) {
                // Get accounts by student role
                data = accountRepository.findAccountsByRole(RoleEnum.STUDENT);
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

    public Response<AccountInfoAdmin> deleteAccount(Long accountId) {
        checkAccount();

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
}
