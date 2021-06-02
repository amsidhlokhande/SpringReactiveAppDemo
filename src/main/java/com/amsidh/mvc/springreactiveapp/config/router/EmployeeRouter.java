package com.amsidh.mvc.springreactiveapp.config.router;

import com.amsidh.mvc.springreactiveapp.handler.EmployeeHandler;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@AllArgsConstructor
@Slf4j
@Configuration
public class EmployeeRouter {

    private static final String EMPLOYEE_BASE_URL = "/employees-info";
    @Bean
    public RouterFunction<ServerResponse> employeeRoute(EmployeeHandler employeeHandler) {

        /*return RouterFunctions.route().path(EMPLOYEE_BASE_URL, builder ->
                builder.GET("/pagination", employeeHandler.getEmployeeByPagination())
                        .GET("/{employeeId}", employeeHandler.findEmployeeById())
                        .GET(accept(APPLICATION_JSON), employeeHandler.findAllEmployees())
                        .DELETE("/{employeeId}", employeeHandler.deleteEmployeeById())
                        .POST(employeeHandler.postEmployee())
                        .PUT("/{employeeId}", employeeHandler.updateEmployee())
        ).build();*/


        return SpringdocRouteBuilder.route().GET(EMPLOYEE_BASE_URL + "/pagination", employeeHandler.getEmployeeByPagination(),
                builder -> builder.beanClass(EmployeeService.class).beanMethod("getEmployeePaging").build()).GET(EMPLOYEE_BASE_URL + "/{employeeId}", employeeHandler.findEmployeeById(),
                builder -> builder.beanClass(EmployeeService.class).beanMethod("getEmployee").build()).GET(EMPLOYEE_BASE_URL, employeeHandler.findAllEmployees(), builder -> builder.beanClass(EmployeeService.class).beanMethod("getEmployees"))
                .build()
                .and(SpringdocRouteBuilder.route()
                        .POST(EMPLOYEE_BASE_URL, RequestPredicates.accept(MediaType.APPLICATION_JSON)
                                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                                employeeHandler.postEmployee(),
                                builder -> builder.beanClass(EmployeeService.class).beanMethod("createEmployee").build())
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .DELETE(EMPLOYEE_BASE_URL + "/{employeeId}", employeeHandler.deleteEmployeeById(),
                                builder -> builder.beanClass(EmployeeService.class).beanMethod("deleteEmployee").build())
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .PUT(EMPLOYEE_BASE_URL + "/{employeeId}", RequestPredicates.accept(MediaType.APPLICATION_JSON)
                                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                                employeeHandler.updateEmployee(),
                                builder -> builder.beanClass(EmployeeService.class).beanMethod("updateEmployee").build())
                        .build())
                ;
    }
}
