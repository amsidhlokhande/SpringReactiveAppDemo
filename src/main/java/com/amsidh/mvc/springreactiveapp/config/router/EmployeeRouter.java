package com.amsidh.mvc.springreactiveapp.config.router;

import com.amsidh.mvc.springreactiveapp.handler.EmployeeHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@AllArgsConstructor
@Slf4j
@Configuration
public class EmployeeRouter {

    private static final String EMPLOYEE_BASE_URL = "/employees";
    @Bean
    public RouterFunction<ServerResponse> employeeRoute(EmployeeHandler employeeHandler) {

        return RouterFunctions.route().path(EMPLOYEE_BASE_URL, builder ->
                builder.GET("/pagination", employeeHandler.getEmployeeByPagination())
                        .GET("/{employeeId}", employeeHandler.findEmployeeById())
                        .GET(accept(APPLICATION_JSON), employeeHandler.findAllEmployees())
                        .DELETE("/{employeeId}", employeeHandler.deleteEmployeeById())
                        .POST(employeeHandler.postEmployee())
                        .PUT("/{employeeId}", employeeHandler.updateEmployee())
        ).build();


       /* return SpringdocRouteBuilder.route()
                .GET(EMPLOYEE_BASE_URL, employeeHandler.findAllEmployees(), builder -> {
                    builder.beanClass(EmployeeService.class).beanMethod("getEmployees");
                })
                .build()
                .and(SpringdocRouteBuilder.route()
                        .GET(EMPLOYEE_BY_EMPLOYEE_ID_URL, employeeHandler.findEmployeeById(),
                                builder -> {
                                    builder.beanClass(EmployeeService.class).beanMethod("getEmployee").build();
                                })
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .POST(EMPLOYEE_BASE_URL, RequestPredicates.accept(MediaType.APPLICATION_JSON)
                                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                                employeeHandler.postEmployee(),
                                builder -> {
                                    builder.beanClass(EmployeeService.class).beanMethod("createEmployee").build();
                                })
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .DELETE(EMPLOYEE_BY_EMPLOYEE_ID_URL, employeeHandler.deleteEmployeeById(),
                                builder -> {
                                    builder.beanClass(EmployeeService.class).beanMethod("deleteEmployee").build();
                                })
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .PUT(EMPLOYEE_BY_EMPLOYEE_ID_URL, RequestPredicates.accept(MediaType.APPLICATION_JSON)
                                        .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                                employeeHandler.updateEmployee(),
                                builder -> {
                                    builder.beanClass(EmployeeService.class).beanMethod("updateEmployee").build();
                                })
                        .build())
                .and(SpringdocRouteBuilder.route()
                        .GET(EMPLOYEE_PAGINATE_URL, employeeHandler.getEmployeeByPagination(),
                                builder -> {
                                    builder.beanClass(EmployeeService.class).beanMethod("getEmployeePaging").build();
                                })
                        .build());*/
    }
}
