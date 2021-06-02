package com.amsidh.mvc.springreactiveapp.service;

import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.boot.context.properties.bind.DefaultValue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EmployeeService {

    @Operation(operationId = "createEmployee", description = "This method for saving Employee", tags = {
            "createEmployee"})
    Mono<EmployeeVO> createEmployee(@RequestBody(required = true, content = @Content(schema = @Schema(implementation = EmployeeVO.class))) EmployeeVO employeeVO);


    @Operation(operationId = "getEmployee", description = "This method for getting Employee", tags = {
            "getEmployee"})
    Mono<EmployeeVO> getEmployee(@Parameter(in = ParameterIn.PATH, required = true, name = "employeeId") UUID id);


    @Operation(operationId = "updateEmployee", description = "This method for update Employee", tags = {
            "updateEmployee"})
    Mono<EmployeeVO> updateEmployee(@Parameter(in = ParameterIn.PATH, required = true, name = "employeeId") UUID id, @RequestBody(required = true, content = @Content(schema = @Schema(implementation = EmployeeVO.class))) Mono<EmployeeVO> monoEmployeeVO);

    @Operation(operationId = "deleteEmployee", description = "This method for delete Employee", tags = {
            "deleteEmployee"})
    Mono<Void> deleteEmployee(@Parameter(in = ParameterIn.PATH, required = true, name = "employeeId") UUID id);

    @Operation(operationId = "getEmployees", description = "This method is for getting all the Employees", tags = {
            "getEmployees"})
    Flux<EmployeeVO> getEmployees();

    @Operation(operationId = "getEmployeePaging", description = "This method is for getting all the employees based of pagination parameter", tags = {
            "getEmployeePaging"})
    Mono<EmployeePageList> getEmployeePaging(@Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(defaultValue = "Amsidh", type = "string")) String name,
                                             @Parameter(in = ParameterIn.QUERY, name = "email", schema = @Schema(defaultValue = "amsidh@gmail.com", type = "string")) String email,
                                             @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(defaultValue = "0", type = "integer")) Integer pageNumber,
                                             @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(defaultValue = "20", type = "integer")) @DefaultValue(value = "20") Integer pageSize);
}
