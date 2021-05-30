package com.amsidh.mvc.springreactiveapp.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String exceptionMessage) {
        super(exceptionMessage);
    }
}