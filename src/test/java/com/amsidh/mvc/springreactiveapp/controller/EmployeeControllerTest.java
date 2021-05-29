package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
@Import(EmployeeService.class)
public class EmployeeControllerTest {

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testGetEmployeeById() {
        final EmployeeVO employeeVO = EmployeeVO.builder().name("Amsidh").email("amsidhlokhande@gmail.com").build();

        Mockito.when(employeeService.getEmployee(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Mono.just(employeeVO));
        webClient.get().uri("/employees/{employeeId}", UUID.randomUUID())
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeVO.class)
                .value(empVO -> Assertions.assertEquals(employeeVO.getEmail(), empVO.getEmail()));
        Mockito.verify(employeeService, Mockito.times(1)).getEmployee(ArgumentMatchers.any(UUID.class));
    }

    @Test
    public void testGetEmployeeByIdNotFound() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeService.getEmployee(ArgumentMatchers.any(UUID.class)))
                .thenThrow(new EmployeeNotFoundException(uuid));
        webClient.get().uri("/employees/{employeeId}", uuid)
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isNotFound();
        Mockito.verify(employeeService, Mockito.times(1)).getEmployee(ArgumentMatchers.any(UUID.class));
    }

    @Test
    public void testFindAllEmployee() {
        List<EmployeeVO> employeeVOList = Arrays.asList(EmployeeVO.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build(),
                EmployeeVO.builder().name("Adithi Lokhande").email("adithi@gmail.com").build(),
                EmployeeVO.builder().name("Aditya Lokhande").email("aditya@gmail.com").build());
        Mockito.when(employeeService.getEmployees()).thenReturn(Flux.fromIterable(employeeVOList));

        webClient.get().uri("/employees")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<EmployeeVO>>() {
                })
                .value(emp -> Assertions.assertFalse(emp.isEmpty()));

        Mockito.verify(employeeService, Mockito.times(1)).getEmployees();
    }

    @Test
    public void testPostEmployee() {
        EmployeeVO employeeVO = EmployeeVO.builder().id(UUID.randomUUID()).name("Amsidh Lokhande").email("amsidh@gmail.com").build();
        Mockito.when(employeeService.createEmployee(ArgumentMatchers.any(EmployeeVO.class))).thenReturn(Mono.just(employeeVO));
        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.mutateWith(csrf()).post().uri("/employees")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .body(BodyInserters.fromValue(EmployeeVO.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeVO.class)
                .value(empVo -> Assertions.assertEquals(empVo.getName(), employeeVO.getName()));
        Mockito.verify(employeeService, Mockito.times(1)).createEmployee(ArgumentMatchers.any(EmployeeVO.class));
    }

}
