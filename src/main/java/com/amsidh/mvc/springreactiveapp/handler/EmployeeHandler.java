package com.amsidh.mvc.springreactiveapp.handler;

import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Component
public class EmployeeHandler {

    private final EmployeeService employeeService;

    public HandlerFunction<ServerResponse> findAllEmployees() {
        log.info("EmployeeHandler findAllEmployees method called");
        return serverRequest -> ServerResponse.ok().body(employeeService.getEmployees(), EmployeeVO.class);
    }

    public HandlerFunction<ServerResponse> findEmployeeById() {
        log.info("EmployeeHandler findEmployeeById method called");
        return serverRequest -> ServerResponse.ok().body(employeeService.getEmployee(UUID.fromString(serverRequest.pathVariable("employeeId"))), EmployeeVO.class);
    }

    public HandlerFunction<ServerResponse> postEmployee() {
        log.info("EmployeeHandler postEmployee method called");
        return serverRequest -> serverRequest.bodyToMono(EmployeeVO.class).flatMap(employeeVO -> ServerResponse.ok().body(employeeService.createEmployee(employeeVO), EmployeeVO.class));
    }

    public HandlerFunction<ServerResponse> updateEmployee() {
        log.info("EmployeeHandler updateEmployee method called");
        return serverRequest -> ServerResponse
                .ok()
                .body(employeeService.updateEmployee(UUID
                                .fromString(serverRequest.pathVariable("employeeId")),
                        serverRequest.bodyToMono(EmployeeVO.class)),
                        EmployeeVO.class);
    }

    public HandlerFunction<ServerResponse> deleteEmployeeById() {
        log.info("EmployeeHandler deleteEmployeeById method called");
        return serverRequest -> ServerResponse.ok().body(employeeService.deleteEmployee(UUID
                .fromString(serverRequest.pathVariable("employeeId"))), Void.class);
    }

    public HandlerFunction<ServerResponse> getEmployeeByPagination() {
        log.info("EmployeeHandler getEmployeeByPagination method called");
        return serverRequest -> ServerResponse.ok().body(employeeService.getEmployeePaging(
                serverRequest.queryParam("name").orElse(""), serverRequest.queryParam("email").orElse(""),
                Integer.parseInt(serverRequest.queryParam("pageNumber").orElse("0")), Integer.parseInt(serverRequest.queryParam("pageSize").orElse("20"))), EmployeePageList.class);
    }
}
