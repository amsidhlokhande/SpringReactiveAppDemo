package com.amsidh.mvc.springreactiveapp.controller;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
                .expectBody(EmployeeVO.class).value(employeeVO1 -> Assertions.assertEquals(employeeVO.getEmail(), employeeVO.getEmail()));

        Mockito.verify(employeeService, Mockito.times(1)).getEmployee(ArgumentMatchers.any(UUID.class));

    }


}
