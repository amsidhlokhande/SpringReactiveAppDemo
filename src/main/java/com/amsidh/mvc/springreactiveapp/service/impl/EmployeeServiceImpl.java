package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeUpdateException;
import com.amsidh.mvc.springreactiveapp.exception.NoDataFoundException;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            return employeeRepository.saveAndFlush(employee);
        }).orElseThrow(() -> new EmployeeUpdateException(id, employeeVO));
        return objectMapper.convertValue(updatedEmployee, EmployeeVO.class);
    }

    @Override
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeVO> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) throw new NoDataFoundException();
        CollectionType listOfEmployeeVOType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, EmployeeVO.class);
        return objectMapper.convertValue(employees, listOfEmployeeVOType);
    }
}
