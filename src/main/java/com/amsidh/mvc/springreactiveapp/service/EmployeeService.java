package com.amsidh.mvc.springreactiveapp.service;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    Mono<EmployeeVO> createEmployee(EmployeeVO employeeVO);

    Mono<EmployeeVO> getEmployee(UUID id);

    Mono<EmployeeVO> updateEmployee(UUID id, EmployeeVO employeeVO);

    Mono<Void> deleteEmployee(UUID id);

    Flux<EmployeeVO> getEmployees(Integer pageNo, Integer pageSize, String sortBy);
}
