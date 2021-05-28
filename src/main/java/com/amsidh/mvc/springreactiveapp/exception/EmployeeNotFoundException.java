package com.amsidh.mvc.springreactiveapp.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(UUID id) {
        super(String.format("Employee with EmployeeId %s not found", id));
    }
}
