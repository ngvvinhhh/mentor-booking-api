package com.swd392.mentorbooking.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.auth.*;
import com.swd392.mentorbooking.dto.otp.VerifyOTPRequestDTO;
import com.swd392.mentorbooking.dto.otp.VerifyOTPResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.entity.Wallet;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.jwt.JWTService;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.WalletRepository;
import com.swd392.mentorbooking.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin("**")
@SecurityRequirement(name = "api")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WalletRepository walletRepository;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerAccount(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.registerAccount(registerRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.checkLogin(loginRequestDTO);
    }

    @PostMapping("/login/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody String idTokenEmail) {

        String email = idTokenEmail;
        String name = email.split("@")[0];

        Account existingUser = accountRepository.findByEmail(idTokenEmail).orElse(null);

        //Tạo tài khoản
        if (existingUser == null) {
            existingUser = new Account();
            existingUser.setEmail(email);
            existingUser.setName(name);
            existingUser.setAvatar("https://firebasestorage.googleapis.com/v0/b/mentor-booking-3d46a.appspot.com/o/76f15d2d-9f0b-4051-8177-812d5ee785a1.jpg?alt=media&token=b600c9a4-cf33-4863-85e1-7ecbccee65f6");
            existingUser.setRole(RoleEnum.STUDENT);
            existingUser.setIsDeleted(false);
            existingUser.setPassword(passwordEncoder.encode("1"));
            existingUser.setStatus(AccountStatusEnum.VERIFIED);
            accountRepository.save(existingUser);

            //Create wallet as the account is created here
            Wallet wallet = Wallet.builder()
                    .account(existingUser)
                    .total(0.0)
                    .build();
            walletRepository.save(wallet);
        }

        existingUser.setTokens(jwtService.generateToken(existingUser.getEmail()));
        existingUser.setRefreshToken(jwtService.generateRefreshToken(existingUser.getEmail()));

        return login(new LoginRequestDTO(existingUser.getEmail(), "1"));
    }

    @PostMapping("/login/google/mobile")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody MobileGoogleRegisterDTO mobileGoogleRegisterDTO) {

        String email = mobileGoogleRegisterDTO.getEmail();
        String name = mobileGoogleRegisterDTO.getName();
        String picture = mobileGoogleRegisterDTO.getPicture();

        Account existingUser = accountRepository.findByEmail(email).orElse(null);

        //Tạo tài khoản
        if (existingUser == null) {
            existingUser = new Account();
            existingUser.setEmail(email);
            existingUser.setName(name);
            existingUser.setAvatar(picture);
            existingUser.setRole(RoleEnum.STUDENT);
            existingUser.setIsDeleted(false);
            existingUser.setPassword(passwordEncoder.encode("1"));
            existingUser.setStatus(AccountStatusEnum.VERIFIED);
            accountRepository.save(existingUser);

            //Create wallet as the account is created here
            Wallet wallet = Wallet.builder()
                    .account(existingUser)
                    .total(0.0)
                    .build();
            walletRepository.save(wallet);
        }
        existingUser.setName(name);
        existingUser.setAvatar(picture);

        existingUser.setTokens(jwtService.generateToken(existingUser.getEmail()));
        existingUser.setRefreshToken(jwtService.generateRefreshToken(existingUser.getEmail()));

        return login(new LoginRequestDTO(existingUser.getEmail(), "1"));
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

    @PostMapping("/verify-otp")
    public Response<VerifyOTPResponseDTO> verifyOTP(@RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO) {
        return authService.validateOTP(verifyOTPRequestDTO);
    }
}
