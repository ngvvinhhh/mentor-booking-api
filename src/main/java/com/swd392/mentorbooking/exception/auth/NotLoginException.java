package com.swd392.mentorbooking.exception.auth;

public class NotLoginException extends RuntimeException{
    public NotLoginException(String message){
        super(message);
    }
}
