package com.swd392.mentorbooking.controller;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.auth.*;
import com.swd392.mentorbooking.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
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

    @GetMapping("/delete/")
    public Response<String> deleteAccount() {
        return authService.deleteAccount();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return authService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return authService.resetPassword(resetPasswordRequest, token);

    }

    @PostMapping("/forgot-password-otp")
    public ResponseEntity<ForgotPasswordResponse> forgotPasswordOTP(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return authService.forgotPasswordOTP(forgotPasswordRequest);
    }

    @PostMapping("/reset-password-otp")
    public ResponseEntity<ResetPasswordResponse> resetPasswordOTP(@RequestBody ResetPasswordOTPRequest resetPasswordRequest) {
        return authService.resetPasswordOTP(resetPasswordRequest);

    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<Void> activateAccount(@PathVariable String token) throws Exception {
        if (authService.verifyAccount(token)) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:5173/login")).build();
        }
        return null;
    }
}
