package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.entity.Account;
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

    @GetMapping("/account")
    public Response<List<Account>> getAllAccountByRole(@RequestParam(value = "role", required = false) String role) {
        return adminService.getAllAccountByRole(role);
    }

}
