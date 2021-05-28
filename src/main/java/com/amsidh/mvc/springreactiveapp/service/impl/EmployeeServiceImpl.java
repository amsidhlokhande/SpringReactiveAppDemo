package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeUpdateException;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public EmployeeVO createEmployee(EmployeeVO employeeVO) {
        Employee employee = employeeRepository.save(objectMapper.convertValue(employeeVO, Employee.class));
        return objectMapper.convertValue(employee, EmployeeVO.class);
    }

    @Override
    public EmployeeVO getEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return objectMapper.convertValue(employee, EmployeeVO.class);
    }

    @Override
    public EmployeeVO updateEmployee(UUID id, EmployeeVO employeeVO) {
        Employee updatedEmployee = Optional.ofNullable(employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id))).map(employee -> {
            Optional.ofNullable(employeeVO.getName()).ifPresent(employee::setName);
            Optional.ofNullable(employeeVO.getEmail()).ifPresent(employee::setEmail);
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new EmployeeUpdateException(id, employeeVO));
        return objectMapper.convertValue(updatedEmployee, EmployeeVO.class);
    }

    @Override
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeVO> getEmployees(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        return employeeRepository.findAll(pageable).stream().parallel()
                .map(employee -> objectMapper.convertValue(employee, EmployeeVO.class))
                .collect(Collectors.toList());
    }
}
