package com.swd392.mentorbooking.exception.topic;

import com.swd392.mentorbooking.exception.ErrorCode;

public class TopicException extends RuntimeException{
    private final ErrorCode errorCode;
    public TopicException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}