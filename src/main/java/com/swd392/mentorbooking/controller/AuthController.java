package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.auth.LoginRequestDTO;
import com.swd392.mentorbooking.dto.auth.LoginResponseDTO;
import com.swd392.mentorbooking.dto.auth.RegisterRequestDTO;
import com.swd392.mentorbooking.dto.auth.RegisterResponseDTO;
import com.swd392.mentorbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("**")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerAccount(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
        return authService.registerAccount(registerRequestDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.checkLogin(loginRequestDTO);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword() {

    }

    @PostMapping("/reset-password")
    public void resetPassword() {

    }
}
