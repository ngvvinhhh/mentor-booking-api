package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.admin.AccountInfoAdmin;
import com.swd392.mentorbooking.dto.auth.RegisterRequestDTO;
import com.swd392.mentorbooking.dto.auth.RegisterResponseDTO;
import com.swd392.mentorbooking.dto.blog.GetBlogResponseDTO;
import com.swd392.mentorbooking.dto.websitefeedback.WebsiteFeedbackResponse;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.entity.WebsiteFeedback;
import com.swd392.mentorbooking.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ** ACCOUNT SECTION ** //

    // Get all account
    @GetMapping("/accounts")
    public Response<List<AccountInfoAdmin>> getAllAccountByRole(@RequestParam(value = "role", required = false) String role) {
        return adminService.getAllAccountByRole(role);
    }

    // Delete account
    @DeleteMapping("/account/delete/{accountId}")
    public Response<AccountInfoAdmin> deleteAccount(@PathVariable Long accountId) {
        return adminService.deleteAccount(accountId);
    }

    @PostMapping("/account/add-new-account")
    public ResponseEntity<RegisterResponseDTO> addNewAccount(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return adminService.addNewAccount(registerRequestDTO);
    }

    // Ban account

    // ** BOOKING SECTION ** //

    // Get all bookings
    @GetMapping("/bookings")
    public Response<List<Booking>> getAllBooking() {
        return adminService.getAllBooking();
    }

    // ** TOPIC SECTION ** //

    // Get all topics
    @GetMapping("/topics")
    public Response<List<Topic>> getAllTopic() {
        return adminService.getAllTopic();
    }

    // ** BLOG SECTION ** //

    // Get all blogs
    @GetMapping("/blogs")
    public Response<List<GetBlogResponseDTO>> getAllBlog() {
        return adminService.viewAllBlogs();
    }

    // ** WEBSITE FEEDBACK SECTION ** //

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/website-feedbacks")
    @Operation(summary = "Lấy tất cả website feedbacks ra cho admin đọc",
            description = "Phương thức này trả về các blog kể cả đã bị xoá cho admin đọc.")
    public Response<List<WebsiteFeedbackResponse>> getAllFeedbackWebsite() {
        return adminService.getAllFeedbackWebsite();
    }
}
