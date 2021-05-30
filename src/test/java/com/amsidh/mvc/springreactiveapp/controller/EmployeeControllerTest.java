package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.exception.BadRequestException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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

    @Test
    public void testPostEmployeeBadRequest() {
        Mockito.when(employeeService.createEmployee(ArgumentMatchers.any(EmployeeVO.class))).thenThrow(new BadRequestException("Bad Request"));
        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.mutateWith(csrf()).post().uri("/employees")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .body(BodyInserters.fromValue(EmployeeVO.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build()))
                .exchange()
                .expectStatus().isBadRequest();
        Mockito.verify(employeeService, Mockito.times(1)).createEmployee(ArgumentMatchers.any(EmployeeVO.class));
    }

    @Test
    public void testDeleteEmployee() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeService.deleteEmployee(ArgumentMatchers.any(UUID.class))).thenReturn(Mono.empty());
        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.mutateWith(csrf()).delete().uri("/employees/{employeeId}", uuid)
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isOk();
        Mockito.verify(employeeService, Mockito.times(1)).deleteEmployee(ArgumentMatchers.any(UUID.class));
    }

    @Test
    public void testDeleteEmployeeNotFound() {
        UUID uuid = UUID.randomUUID();
        Mockito.when(employeeService.deleteEmployee(ArgumentMatchers.any(UUID.class))).thenThrow(new EmployeeNotFoundException(uuid));
        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.mutateWith(csrf()).delete().uri("/employees/{employeeId}", uuid)
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isNotFound();
        Mockito.verify(employeeService, Mockito.times(1)).deleteEmployee(ArgumentMatchers.any(UUID.class));
    }

    @Test
    public void testUpdateEmployee() {
        UUID uuid = UUID.randomUUID();
        EmployeeVO requestEmployeeVO = EmployeeVO.builder().id(uuid).name("Amsidh Lokhande").email("amsidh@gmail.com").build();
        EmployeeVO responseEmployeeVO = EmployeeVO.builder().id(uuid).name("Amsidh Lokhande Updated").email("amsidh-updated@gmail.com").build();

        Mockito.when(employeeService.updateEmployee(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(Mono.class))).thenReturn(Mono.just(responseEmployeeVO));
        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.mutateWith(csrf()).put().uri("/employees/{employeeId}", uuid)
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .body(BodyInserters.fromValue(requestEmployeeVO))
                .exchange()
                .expectStatus().isOk().expectBody(EmployeeVO.class)
                .value(empVO -> {
                    Assertions.assertEquals(empVO.getEmail(), responseEmployeeVO.getEmail());
                    Assertions.assertEquals(empVO.getName(), responseEmployeeVO.getName());
                });
        Mockito.verify(employeeService, Mockito.times(1)).updateEmployee(ArgumentMatchers.any(UUID.class), ArgumentMatchers.any(Mono.class));
    }

    @Test
    public void testGetEmployeePagingWithPageNUmber1AndPageSize10() {
        List<EmployeeVO> employeeVOList = getEmployeeVOS();
        int pageNumber = 1;
        Integer pageSize = 10;
        EmployeePageList employeeVOS = new EmployeePageList(employeeVOList, PageRequest.of(pageNumber, pageSize), employeeVOList.size());
        Mockito.when(employeeService.getEmployeePaging(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Mono.just(employeeVOS));

        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.get().uri("/employees/pagination")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .exchange()
                .expectStatus().isOk().expectBody(EmployeePageList.class)
                .value(employeePageList -> Optional.ofNullable(employeePageList).ifPresent(empList -> Assertions.assertFalse(empList.isEmpty())));
        Mockito.verify(employeeService, Mockito.times(1))
                .getEmployeePaging(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    public void testGetEmployeePagingWithEmailRequestParam() {
        List<EmployeeVO> employeeVOList = getEmployeeVOS();
        int pageNumber = 1;
        Integer pageSize = 10;
        String email = "amsidh@gmail.com";
        EmployeePageList employeeVOS = new EmployeePageList(employeeVOList.stream()
                .filter(employeeVO -> employeeVO.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList()),
                PageRequest.of(pageNumber, pageSize),
                employeeVOList.size());

        Mockito.when(employeeService.getEmployeePaging(
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(String.class),
                ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(Mono.just(employeeVOS));

        //CSRF Token has been associated to this client. So using webClient.mutateWith(csrf())
        webClient.get().uri("/employees/pagination")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("user" + ":" + "password").getBytes(StandardCharsets.UTF_8)))
                .attribute("email", email)
                .exchange()
                .expectStatus().isOk().expectBody(EmployeePageList.class)
                .value(employeePageList -> Optional.ofNullable(employeePageList).ifPresent(empList -> {
                    Assertions.assertFalse(empList.isEmpty());
                    empList.forEach(employeeVO -> Assertions.assertTrue(employeeVO.getEmail().equalsIgnoreCase(email)));
                }));
        Mockito.verify(employeeService, Mockito.times(1))
                .getEmployeePaging(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(PageRequest.class));
    }

    private List<EmployeeVO> getEmployeeVOS() {
        List<EmployeeVO> employeeVOList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i < 30) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Amsidh Lokhande").email("amsidh@gmail.com").build());
            } else if (30 < i && i < 50) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Anjali Lokhande").email("anjali@gmail.com").build());
            } else if (50 < i && i < 70) {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Adithi Lokhande").email("adithi@gmail.com").build());
            } else {
                employeeVOList.add(EmployeeVO.builder().id(UUID.randomUUID()).name("Aditya Lokhande").email("aditya@gmail.com").build());
            }

        }
        return employeeVOList;
    }
}
