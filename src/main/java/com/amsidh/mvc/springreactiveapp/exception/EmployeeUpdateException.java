package com.amsidh.mvc.springreactiveapp.exception;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;

import java.util.UUID;

public class EmployeeUpdateException extends RuntimeException {

    public EmployeeUpdateException(UUID id, EmployeeVO employeeVO) {
        super(String.format("Update employee with employee %s failed. Employee Details are %s", id, employeeVO.toString()));
    }
}
