package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public Flux<EmployeeVO> findAllEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping("/{employeeId}")
    public Mono<EmployeeVO> findEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping
    public Mono<EmployeeVO> saveEmployee(@RequestBody EmployeeVO employeeVO) {
        return employeeService.createEmployee(employeeVO);
    }

    @PutMapping("/{employeeId}")
    public Mono<EmployeeVO> updateEmployeeById(@PathVariable("employeeId") UUID employeeId, @RequestBody Mono<EmployeeVO> monoEmployeeVO) {
        return employeeService.updateEmployee(employeeId, monoEmployeeVO);
    }

    @DeleteMapping("/{employeeId}")
    public Mono<Void> deleteEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

}
