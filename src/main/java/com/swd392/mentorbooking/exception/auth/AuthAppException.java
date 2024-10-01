package com.swd392.mentorbooking.exception.auth;

import com.swd392.mentorbooking.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthAppException extends RuntimeException{
    private ErrorCode errorCode;

    public AuthAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
