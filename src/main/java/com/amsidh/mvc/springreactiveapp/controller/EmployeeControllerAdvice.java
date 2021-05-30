package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.exception.BadRequestException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeUpdateException;
import com.amsidh.mvc.springreactiveapp.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverExceptionHandler(Exception ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String employeeNotFoundException(EmployeeNotFoundException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NoDataFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String employeeNotDataFoundException(NoDataFoundException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(EmployeeUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String employeeNotDataFoundException(EmployeeUpdateException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String employeeNotDataFoundException(BadRequestException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    private void logToConsole(Exception ex) {
        if (log.isDebugEnabled()) {
            log.error(ex.getMessage(), ex);
        } else {
            log.error(ex.getLocalizedMessage());
        }
    }
}
