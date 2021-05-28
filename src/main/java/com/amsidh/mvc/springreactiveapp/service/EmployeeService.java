package com.amsidh.mvc.springreactiveapp.service;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeVO createEmployee(EmployeeVO employeeVO);

    EmployeeVO getEmployee(UUID id);

    EmployeeVO updateEmployee(UUID id, EmployeeVO employeeVO);

    void deleteEmployee(UUID id);

    List<EmployeeVO> getEmployees();
}
