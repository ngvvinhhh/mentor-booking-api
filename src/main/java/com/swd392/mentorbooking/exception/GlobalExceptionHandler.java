package com.swd392.mentorbooking.exception;

import com.swd392.mentorbooking.dto.ErrorResponse;
import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.exception.auth.InvalidAccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(InvalidAccountException.class)
    public ResponseEntity<Response<Object>> handleInvalidAccountException(InvalidAccountException ex) {
        Response<Object> response = new Response<>(400, ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
