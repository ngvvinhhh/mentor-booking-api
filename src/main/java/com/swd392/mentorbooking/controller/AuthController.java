package com.swd392.mentorbooking.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("**")
public class AuthController {

    @PostMapping("/login")
    public void Login() {

    }

    @PostMapping("/register")
    public void register() {

    }

    @PostMapping("/forgot-password")
    public void forgotPassword() {

    }

    @PostMapping("/reset-password")
    public void resetPassword() {

    }
}
