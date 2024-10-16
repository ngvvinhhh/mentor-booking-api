package com.swd392.mentorbooking.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.auth.*;
import com.swd392.mentorbooking.email.EmailDetail;
import com.swd392.mentorbooking.email.EmailService;
import com.swd392.mentorbooking.entity.Account;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Wallet;
import com.swd392.mentorbooking.exception.ErrorCode;
import com.swd392.mentorbooking.exception.auth.AuthAppException;
import com.swd392.mentorbooking.jwt.JWTService;
import com.swd392.mentorbooking.repository.AccountRepository;
import com.swd392.mentorbooking.repository.WalletRepository;
import com.swd392.mentorbooking.utils.AccountUtils;
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

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final static String defaultAvatar = "https://firebasestorage.googleapis.com/v0/b/mentor-booking-3d46a.appspot.com/o/76f15d2d-9f0b-4051-8177-812d5ee785a1.jpg?alt=media";

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

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private WalletRepository walletRepository;

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
        account.setAvatar(defaultAvatar);

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
            if (account.getIsDeleted().equals(true)) {
                throw new AuthAppException(ErrorCode.ACCOUNT_IS_DELETED);
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
                    200,
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
                    400,
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
            Wallet wallet = Wallet.builder()
                    .account(account)
                    .total(0.0)
                    .build();
            walletRepository.save(wallet);

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
                    .msgBody("Dear " + account.getName() + ",\n\n" +
                            "We received a request to reset the password for your account. To complete the process, please click the link below:\n\n" +
                            "<a href=\"https://circuit-project.vercel.app/forgotPassword?" + token + "\">Reset My Password</a>\n\n" +
                            "If you did not request a password reset, please ignore this email or contact support if you have any concerns.\n\n" +
                            "Thank you,\nThe Support Team")
                    .subject("Password Reset Request - Action Required")
                    .name(account.getName())
                    .build();
            emailService.sendForgotPasswordEmail(emailDetail);

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

    public boolean verifyAccount(String token) throws Exception {
        try {
            String email = jwtService.extractEmail(token);

            Account account = accountRepository.findByEmail(email).orElse(null);
            if (account == null) {
                throw new AuthAppException(ErrorCode.EMAIL_NOT_FOUND);
            }
            account.setStatus(AccountStatusEnum.VERIFIED);
            accountRepository.save(account);
            return true;
        } catch (Exception e) {
            throw new TokenExpiredException("Invalid or expired token!", Instant.now());
        }
    }

    public Response<String> deleteAccount() {
        // Get the current account
        Account account = accountUtils.getCurrentAccount();
        if (account == null) return new Response<>(401, "Please login first", null);

        //Check if account really exist
        account = accountRepository.findById(account.getId()).orElse(null);
        if (account == null) return new Response<>(401, "Account not found", null);

        //Set account status to deleted
        account.setIsDeleted(true);
        accountRepository.save(account);

        return new Response<>(200, "Delete account successfully.", "The account with email " + account.getEmail() + " is deleted");
    }
}
