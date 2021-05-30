package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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


    //Pagination List<EmployeeVO>
    @GetMapping("/pagination")
    public Mono<EmployeePageList> getEmployeePaging(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                    @RequestParam(value = "email", required = false, defaultValue = "") String email,
                                                    @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Mono<EmployeePageList> employeePaging = employeeService.getEmployeePaging(name, email, PageRequest.of(pageNumber, pageSize));
        return employeePaging;
    }
}
