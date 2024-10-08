package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.auth.*;
import com.swd392.mentorbooking.email.EmailDetail;
import com.swd392.mentorbooking.email.EmailService;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.jwt.JWTService;
import com.swd392.mentorbooking.repository.AccountRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private static final String defaultAvatar = "logo.png";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    //Please do not touch
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElse(null);
    }

    private @NotNull Account convertToAccount(RegisterRequestDTO registerRequestDTO) {
        Account account = new Account(
                registerRequestDTO.getName(),
                registerRequestDTO.getEmail(),
                registerRequestDTO.getRole(),
                AccountStatusEnum.UNVERIFIED
        );

        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        return account;
    }

    private String getString(String token) {
        String email = jwtService.extractEmail(token);
        if (email == null || email.isEmpty()) {
            throw new AuthAppException(ErrorCode.TOKEN_INVALID);
        }
        return email;
    }

    public ResponseEntity<LoginResponseDTO> checkLogin(LoginRequestDTO loginRequestDTO) {
        try {
            // GET EMAIL BY REQUEST DTO AND VALIDATION EMAIL
            Account account = findAccountByEmail(loginRequestDTO.getEmail());

            if (account == null) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.ACCOUNT_NOT_VERIFY);
            }
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getEmail(),
                                loginRequestDTO.getPassword()
                        )
                );
            } catch (Exception e) {
                throw new AuthAppException(ErrorCode.USERNAME_PASSWORD_NOT_CORRECT);
            }


            Account returnAccount = (Account) authentication.getPrincipal();
            // CALL FUNC || GENERATE TOKEN (1DAY) AND REFRESH TOKEN (7DAYS)
            account.setTokens(jwtService.generateToken(account.getEmail()));
            account.setRefreshToken(jwtService.generateRefreshToken(account.getEmail()));

            String responseString = "Login successful";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    responseString,
                    null,
                    returnAccount.getTokens(),
                    returnAccount.getRefreshToken()
            );
            return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);


        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorResponse = "Login failed";
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                    e.getMessage(),
                    errorResponse,
                    null,
                    null
            );
            return new ResponseEntity<>(loginResponseDTO, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<RegisterResponseDTO> registerAccount(RegisterRequestDTO registerRequestDTO) {
        try {
            //Check if the email exist
            Account tempAccount = findAccountByEmail(registerRequestDTO.getEmail());
            if (tempAccount != null) {
                if (tempAccount.getStatus().equals(AccountStatusEnum.VERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_EXISTED);
                } else if (tempAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                    throw new AuthAppException(ErrorCode.EMAIL_WAIT_VERIFY);
                }
            }
            Account account = convertToAccount(registerRequestDTO);
            accountRepository.save(account);

            //Create wallet as the account is created here

            String responseMessage = "Successful registration, please check your email for verification";
            RegisterResponseDTO response = new RegisterResponseDTO(responseMessage, null, 201, registerRequestDTO.getEmail());

            //Send email here
            EmailDetail emailDetail = EmailDetail.builder()
                    .recipient(account.getEmail())
                    .msgBody("Please verify your account to continue.")
                    .subject("Please verify your account!")
                    .name(account.getName())
                    .build();
            emailService.sendVerifyEmail(emailDetail);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            String errorMessage = "Register failed";
            RegisterResponseDTO response = new RegisterResponseDTO(errorMessage, errorCode.getMessage(), errorCode.getCode(), null);
            return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }
    }
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            // CHECK VALID EMAIL
            Optional<Account> tempAccount = accountRepository.findByEmail(forgotPasswordRequest.getEmail());

            Account checkAccount = tempAccount.orElseThrow(() -> new AuthAppException(ErrorCode.EMAIL_NOT_FOUND));

            if (checkAccount.getEmail() == null || checkAccount.getEmail().isEmpty() || checkAccount.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            // GENERATE TOKEN FOR EMAIL FORGOT PASSWORD (ENSURE UNIQUE AND JUST ONLY EMAIL CAN USE)
            String token = jwtService.generateToken(forgotPasswordRequest.getEmail());
            Account account = tempAccount.orElseThrow(() -> new UsernameNotFoundException("User not found"));
            account.setTokens(token);

            //SEND MAIL
            EmailDetail emailDetail = EmailDetail.builder()
                    .recipient(account.getEmail())
                    .msgBody("Reset your password account.")
                    .subject("Reset password!")
                    .attachment("https://circuit-project.vercel.app/forgotPassword?" + jwtService.generateToken(account.getEmail()))
                    .name(account.getName())
                    .build();
            emailService.sendEmail(emailDetail);

            accountRepository.save(account);
            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(forgotPasswordResponse, HttpStatus.OK);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(forgotPasswordResponse, errorCode.getHttpStatus());
        }
    }

    public ResponseEntity<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        try {
            // AFTER USER CLICK LINK FORGOT PASSWORD IN EMAIL THEN REDIRECT TO API HERE (RESET PASSWORD)
            // CHECK PASSWORD AND REPEAT PASSWORD
            if (!resetPasswordRequest.getNew_password().equals(resetPasswordRequest.getRepeat_password())) {
                throw new AuthAppException(ErrorCode.PASSWORD_REPEAT_INCORRECT);
            }
            // CALL FUNC
            String email = getString(token);
            // FIND EMAIL IN DATABASE AND UPDATE NEW PASSWORD
            Optional<Account> accountOptional = accountRepository.findByEmail(email);
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();
                account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNew_password()));
                accountRepository.save(account);
            }

            ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse("Password reset token generated successfully.", null, 200);
            return new ResponseEntity<>(resetPasswordResponse, HttpStatus.CREATED);
        } catch (AuthAppException e) {
            ErrorCode errorCode = e.getErrorCode();
            ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse("Password reset failed", e.getMessage(), errorCode.getCode());
            return new ResponseEntity<>(resetPasswordResponse, errorCode.getHttpStatus());
        }

    }

}
