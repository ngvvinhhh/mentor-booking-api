package com.swd392.mentorbooking.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.swd392.mentorbooking.dto.ErrorResponse;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;

import java.nio.file.AccessDeniedException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = new ArrayList<>(); // Tạo danh sách để lưu các thông báo lỗi
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.add(error.getField() + ": " + error.getDefaultMessage()) // Thêm thông báo lỗi vào danh sách
        );

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> ForbiddenException(ForbiddenException ex) {
        List<String> errorMessages = new ArrayList<>(); // Tạo danh sách để lưu các thông báo lỗi
        errorMessages.add(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), errorMessages);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
        String errorMessage = "Invalid date and time format. Please use the correct format: yyyy-MM-dd'T'HH:mm";
        return ResponseEntity.badRequest().body(errorMessage);
    }

    // Handler cho NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response<String>> handleNotFoundException(NotFoundException ex) {
        Response<String> response = new Response<>(404, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        String message = "You do not have permission to access this resource!";
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        String message = "Token has expired!";
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        // Kiểm tra loại lỗi (chẳng hạn khi hết hạn token)
        if (ex instanceof CredentialsExpiredException) {
            return new ResponseEntity<>("Token expired. Please log in again.", HttpStatus.UNAUTHORIZED);
        } else if (ex instanceof BadCredentialsException) {
            return new ResponseEntity<>("Invalid credentials.", HttpStatus.UNAUTHORIZED);
        }

        // Các lỗi khác trả về thông báo chung
        return new ResponseEntity<>("Authentication error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException ex) {
        // Trả về thông báo lỗi và mã trạng thái HTTP 401 (Unauthorized)
        return new ResponseEntity<>("Token has expired. Please log in again.", HttpStatus.UNAUTHORIZED);
    }

}
