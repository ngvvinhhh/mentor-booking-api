package com.swd392.mentorbooking.exception.service;

import com.swd392.mentorbooking.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServiceHandleException {

    @ExceptionHandler(CreateServiceException.class)
    public ResponseEntity<?> CreateServiceException(CreateServiceException createServiceException) {
        Response response = new Response(403, createServiceException.getMessage(), null);
        return ResponseEntity.status(403).body(response);
    }

}
