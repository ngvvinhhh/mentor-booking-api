package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.admin.AccountInfoAdmin;
import com.swd392.mentorbooking.entity.Blog;
import com.swd392.mentorbooking.entity.Booking;
import com.swd392.mentorbooking.entity.Topic;
import com.swd392.mentorbooking.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("**")
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
    public Response<List<Blog>> getAllBlog() {
        return adminService.getAllBlog();
    }
}
