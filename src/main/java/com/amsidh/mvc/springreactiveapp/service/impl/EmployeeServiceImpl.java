package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<EmployeeVO> createEmployee(EmployeeVO employeeVO) {
        return employeeRepository.save(objectMapper.convertValue(employeeVO, Employee.class))
                .map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> getEmployee(Long id) {
        return employeeRepository.findById(id).map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> updateEmployee(Long id, Mono<EmployeeVO> monoEmployeeVO) {
        return this.employeeRepository.findById(id)
                .flatMap(employee -> monoEmployeeVO.map(inputEmployee -> {
                    Optional.ofNullable(inputEmployee.getName()).ifPresent(employee::setName);
                    Optional.ofNullable(inputEmployee.getEmail()).ifPresent(employee::setEmail);
                    return employee;
                })).flatMap(this.employeeRepository::save)
                .map(updatedEmployee -> objectMapper.convertValue(updatedEmployee, EmployeeVO.class));

    }

    @Override
    public Mono<Void> deleteEmployee(Long id) {
        return employeeRepository.deleteById(id);
    }

    @Override
    public Flux<EmployeeVO> getEmployees() {
        return employeeRepository.findAll().map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));

    }
}
