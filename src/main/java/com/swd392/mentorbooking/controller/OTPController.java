package com.swd392.mentorbooking.controller;


import com.swd392.mentorbooking.service.OTPService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/OTP")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class OTPController {

    @Autowired
    private OTPService otpService;

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOTP(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOTP(email, otp);

        if (isValid) {
            return ResponseEntity.ok("OTP is valid. You can reset your password now.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP.");
        }
    }
}
