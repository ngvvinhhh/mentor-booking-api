package com.swd392.mentorbooking.exception;

import com.swd392.mentorbooking.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
