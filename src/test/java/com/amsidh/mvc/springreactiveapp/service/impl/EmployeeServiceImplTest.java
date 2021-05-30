package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.exception.BadRequestException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({EmployeeRepository.class, R2dbcEntityTemplate.class, ObjectMapper.class})
class EmployeeServiceImplTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, r2dbcEntityTemplate, objectMapper);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testCreateEmployee() {
        Mono<Employee> employeeMono = Mono.just(Employee.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build());
        Mockito.when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenReturn(employeeMono);
        Mono<EmployeeVO> employeeVOMono = employeeService.createEmployee(EmployeeVO.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build());
        employeeVOMono.subscribe(Assertions::assertNotNull);
    }

    @Test
    public void testCreateEmployeeNull() {
        Assertions.assertThrows(BadRequestException.class, () -> employeeService.createEmployee(null));
    }

    @Test
    public void testGetEmployee() {
        UUID uuid = UUID.randomUUID();
        Mono<Employee> employeeMono = Mono.just(Employee.builder().id(uuid).name("Amsidh Lokhande").email("amsidh@gmail.com").build());
        Mockito.when(employeeRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(employeeMono);
        Mono<EmployeeVO> employeeVOMono = employeeService.getEmployee(uuid);
        employeeVOMono.subscribe(employeeVO -> {
            Assertions.assertNotNull(employeeVO);
            Assertions.assertEquals(employeeVO.getName(), "Amsidh Lokhande");
            Assertions.assertEquals(employeeVO.getEmail(), "amsidh@gmail.com");
            Assertions.assertEquals(employeeVO.getId(), uuid);
        });
    }

    @Test
    public void testGetEmployeeEmpty() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Mono.empty());
        Mono<EmployeeVO> employeeVOMono = employeeService.getEmployee(uuid);
        EmployeeVO block = employeeVOMono.block();
        Assertions.assertNull(block);
    }


    @Test
    public void updateEmployee() {
        UUID uuid = UUID.randomUUID();
        Mono<Employee> employeeMono = Mono.just(Employee.builder().id(uuid).name("Amsidh Lokhande").email("amsidh@gmail.com").build());
        Employee updateEmployee = Employee.builder().id(uuid).name("Amsidh Lokhande Updated").email("amsidhupdated@gmail.com").build();

        Mockito.when(employeeRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(employeeMono);

        Mockito.when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenReturn(Mono.just(updateEmployee));

        Mono<EmployeeVO> updatedEmployeeVOMono = employeeService.updateEmployee(uuid, Mono.just(objectMapper.convertValue(updateEmployee, EmployeeVO.class)));
        updatedEmployeeVOMono.subscribe(updateEmp -> {
            Assertions.assertNotNull(updateEmp);
            Assertions.assertEquals(updateEmp.getEmail(), updateEmployee.getEmail());
            Assertions.assertEquals(updateEmp.getName(), updateEmployee.getName());
            Assertions.assertEquals(updateEmp.getId(), updateEmployee.getId());
        });

    }

    @Test
    public void updateEmployeeNotFound() {
        UUID uuid = UUID.randomUUID();
        Employee updateEmployee = Employee.builder().id(uuid).name("Amsidh Lokhande Updated").email("amsidhupdated@gmail.com").build();

        Mockito.when(employeeRepository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Mono.empty());

        Mono<EmployeeVO> updatedEmployeeVOMono = employeeService.updateEmployee(uuid, Mono.just(objectMapper.convertValue(updateEmployee, EmployeeVO.class)));
        updatedEmployeeVOMono.subscribe(Assertions::assertNull);

    }


    @Test
    public void deleteEmployee() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeRepository.deleteById(ArgumentMatchers.any(UUID.class))).thenReturn(Mono.empty());
        Mono<Void> deleteEmployeeVOMono = employeeService.deleteEmployee(uuid);
        deleteEmployeeVOMono.subscribe(Assertions::assertNull);
    }

    @Test
    public void deleteEmployeeNotFound() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeRepository.deleteById(ArgumentMatchers.any(UUID.class))).thenThrow(new EmployeeNotFoundException(uuid));
        Assertions.assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(uuid));
    }

    @Test
    public void getEmployees() {
        List<Employee> employees = getEmployeeVOS().parallelStream()
                .map(employeeVO -> objectMapper.convertValue(employeeVO, Employee.class))
                .collect(Collectors.toList());
        Mockito.when(employeeRepository.findAll()).thenReturn(Flux.fromIterable(employees));
        Flux<EmployeeVO> employeesFlux = employeeService.getEmployees();
        employeesFlux.subscribe(employeeVO -> {
            Assertions.assertNotNull(employeeVO);
            Assertions.assertNotNull(employeeVO.getId());
        });
    }

    @Test
    public void getEmployeesEmpty() {
        Mockito.when(employeeRepository.findAll()).thenReturn(Flux.empty());
        Flux<EmployeeVO> employeesFlux = employeeService.getEmployees();
        Assertions.assertNull(employeesFlux.blockFirst());
        Assertions.assertTrue(Objects.requireNonNull(employeesFlux.collectList().block()).isEmpty());
    }

    //@Test
    public void testGetEmployeePaging() {
        List<Employee> employees = getEmployeeVOS().parallelStream()
                .map(employeeVO -> objectMapper.convertValue(employeeVO, Employee.class))
                .collect(Collectors.toList());


        when(r2dbcEntityTemplate.select(Employee.class).all())
                .thenReturn(Flux.fromIterable(employees));

        Mono<EmployeePageList> employeePageListMono = employeeService.
                getEmployeePaging("Amsidh Lokhande", "amsidh@gmail.com", PageRequest.of(1, 10));
        EmployeePageList employeePageList = employeePageListMono.block();
        Assertions.assertNotNull(employeePageList);

    }


    private List<EmployeeVO> getEmployeeVOS() {
        List<EmployeeVO> employeeVOList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i < 30) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Amsidh Lokhande").email("amsidh@gmail.com").build());
            } else if (30 < i && i < 50) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Anjali Lokhande").email("anjali@gmail.com").build());
            } else if (50 < i && i < 70) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Adithi Lokhande").email("adithi@gmail.com").build());
            } else {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Aditya Lokhande").email("aditya@gmail.com").build());
            }

        }
        return employeeVOList;
    }
}