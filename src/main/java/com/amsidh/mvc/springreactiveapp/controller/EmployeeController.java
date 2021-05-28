package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Flux<EmployeeVO>> findAllEmployees(@RequestParam(defaultValue = "0") Integer pageNo,
                                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                                             @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(employeeService.getEmployees(pageNo, pageSize, sortBy));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<Mono<EmployeeVO>> findEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return ResponseEntity.ok(employeeService.getEmployee(employeeId));
    }

    @PostMapping
    public ResponseEntity<Mono<EmployeeVO>> saveEmployee(@RequestBody EmployeeVO employeeVO) {
        return ResponseEntity.ok(employeeService.createEmployee(employeeVO));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<Mono<EmployeeVO>> updateEmployeeById(@PathVariable("employeeId") UUID employeeId, @RequestBody EmployeeVO employeeVO) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, employeeVO));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Mono<Void>> deleteEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

}
