package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeVO> findAllEmployees(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "id") String sortBy) {
        return employeeService.getEmployees(pageNo,pageSize,sortBy);
    }

    @GetMapping("/{employeeId}")
    public EmployeeVO findEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping
    public EmployeeVO saveEmployee(@RequestBody EmployeeVO employeeVO) {
        return employeeService.createEmployee(employeeVO);
    }

    @PutMapping("/{employeeId}")
    public EmployeeVO updateEmployeeById(@PathVariable("employeeId") UUID employeeId, @RequestBody EmployeeVO employeeVO) {
        return employeeService.updateEmployee(employeeId, employeeVO);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

}
