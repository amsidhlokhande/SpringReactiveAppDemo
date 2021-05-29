package com.amsidh.mvc.springreactiveapp.service;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {

    Mono<EmployeeVO> createEmployee(EmployeeVO employeeVO);

    Mono<EmployeeVO> getEmployee(Long id);

    Mono<EmployeeVO> updateEmployee(Long id, Mono<EmployeeVO> monoEmployeeVO);

    Mono<Void> deleteEmployee(Long id);

    Flux<EmployeeVO> getEmployees();
}
