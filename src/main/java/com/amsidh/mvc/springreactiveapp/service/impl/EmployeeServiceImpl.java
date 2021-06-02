package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.exception.BadRequestException;
import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.empty;
import static org.springframework.data.relational.core.query.Query.query;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<EmployeeVO> createEmployee(EmployeeVO employeeVO) {
        Employee employee = Optional.ofNullable(employeeVO)
                .map(empVo -> Employee.builder().name(empVo.getName()).email(empVo.getEmail()).build()).orElseThrow(() -> new BadRequestException("Employee is empty or null"));
        return employeeRepository.save(employee)
                .map(emp -> objectMapper.convertValue(emp, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> getEmployee(UUID id) {
        return employeeRepository.findById(id).defaultIfEmpty(Employee.builder().build()).map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> updateEmployee(UUID id, Mono<EmployeeVO> monoEmployeeVO) {
        log.info("EmployeeServiceImpl updateEmployee method called");
        return this.employeeRepository.findById(id)
                .flatMap(employee -> monoEmployeeVO.map(inputEmployee -> {
                    Optional.ofNullable(inputEmployee.getName()).ifPresent(employee::setName);
                    Optional.ofNullable(inputEmployee.getEmail()).ifPresent(employee::setEmail);
                    return employee;
                })).flatMap(this.employeeRepository::save)
                .map(updatedEmployee -> objectMapper.convertValue(updatedEmployee, EmployeeVO.class));

    }

    @Override
    public Mono<Void> deleteEmployee(UUID id) {
        return employeeRepository.deleteById(id);
    }

    @Override
    public Flux<EmployeeVO> getEmployees() {
        return employeeRepository.findAll().map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeePageList> getEmployeePaging(String name, String email, Integer pageNumber, Integer pageSize) {
        Query query = null;
        if (StringUtils.hasText(name) && StringUtils.hasText(email)) {
            query = query(where("name").is(name).and("email").is(email));
        } else if (StringUtils.hasText(name) && !StringUtils.hasText(email)) {
            query = query(where("name").is(name));
        } else if (!StringUtils.hasText(name) && StringUtils.hasText(email)) {
            query = query(where("email").is(email));
        } else {
            query = empty();
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        return r2dbcEntityTemplate.select(Employee.class).matching(query.with(pageRequest)).all()
                .map(employee -> objectMapper.convertValue(employee, EmployeeVO.class))
                .collect(Collectors.toList())
                .map(employeeVOS -> new EmployeePageList(employeeVOS, pageRequest, employeeVOS.size()));

    }


}
