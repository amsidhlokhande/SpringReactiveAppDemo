package com.amsidh.mvc.springreactiveapp.exception;

public class NoDataFoundException extends RuntimeException {

    public NoDataFoundException() {
        super("No data found");
    }
}