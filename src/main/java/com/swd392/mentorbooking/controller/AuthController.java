package com.swd392.mentorbooking.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.auth.*;
import com.swd392.mentorbooking.dto.otp.VerifyOTPRequestDTO;
import com.swd392.mentorbooking.dto.otp.VerifyOTPResponseDTO;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.RoleEnum;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.jwt.JWTService;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerAccount(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
        return authService.registerAccount(registerRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.checkLogin(loginRequestDTO);
    }

    @PostMapping("/auth/google")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("token");

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String displayName = decodedToken.getName();
            String avatar = decodedToken.getPicture();

            Account account = accountRepository.findByEmail(email).orElse(null);

            //Tạo tài khoản
            if (account == null) {
                account = new Account();
                account.setEmail(email);
                account.setName(displayName);
                account.setAvatar(avatar);
                account.setRole(RoleEnum.STUDENT);
                accountRepository.save(account);
            }
            account.setEmail(email);
            account.setName(displayName);
            account.setAvatar(avatar);
            accountRepository.save(account);

            account.setTokens(jwtService.generateToken(account.getEmail()));
            account.setRefreshToken(jwtService.generateRefreshToken(account.getEmail()));

            String responseString = "Login successful";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    200,
                    responseString,
                    null,
                    account.getRole(),
                    account.getTokens(),
                    account.getRefreshToken()
            );
            return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

        } catch (FirebaseAuthException e) {
            String errorResponse = "Login failed";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    400,
                    e.getMessage(),
                    errorResponse,
                    null,
                    null,
                    null
            );
            return new ResponseEntity<>(loginResponseDTO, ErrorCode.INVALID_OTP.getHttpStatus());
        }
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
    public Response<VerifyOTPResponseDTO> verifyOTP(@RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO){
        return authService.validateOTP(verifyOTPRequestDTO);
    }
}
