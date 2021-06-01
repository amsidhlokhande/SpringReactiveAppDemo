package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeePageList;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.Base64Utils.encodeToString;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebClientIT {

    private static final String BASE_URL = "http://localhost:8080";
    WebClient webClient;

    @BeforeEach
    public void setup() {
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeToString(("user" + ":" + "password").getBytes(UTF_8)))
                .build();
    }

    @Test
    public void testGetEmployeeById() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeeVO> employeeVOAtomicReference = new AtomicReference<>();
        Flux<EmployeeVO> employeeVOFlux = webClient.get().uri("/employees").accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(EmployeeVO.class);
        employeeVOFlux.subscribe(employeeVO -> {
            employeeVOAtomicReference.set(employeeVO);
            log.info(employeeVO.toString());
            countDownLatch.countDown();
        });

        countDownLatch.await();

        Mono<EmployeeVO> employeeVOMono = webClient.get().uri("/employees/{employeeId}", employeeVOAtomicReference.get().getId()).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(EmployeeVO.class);
        employeeVOMono.publishOn(Schedulers.parallel()).subscribe(empVO -> {
            Assertions.assertNotNull(empVO);
            Assertions.assertEquals(empVO.getName(), employeeVOAtomicReference.get().getName());
            Assertions.assertEquals(empVO.getEmail(), employeeVOAtomicReference.get().getEmail());
            Assertions.assertEquals(empVO.getId(), employeeVOAtomicReference.get().getId());
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    @Test
    public void testGetEmployees() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<EmployeeVO> employeeVOFlux = webClient.get().uri("/employees").accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(EmployeeVO.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeeVO -> {
            log.info(employeeVO.toString());
            Assertions.assertNotNull(employeeVO);
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    @Test
    public void testCreateEmployee() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono<EmployeeVO> employeeVOMono = webClient.post().uri("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                        .fromValue(EmployeeVO
                                .builder()
                                .name("TestName")
                                .email("test@gmail.com").build()
                        ))
                .retrieve()
                .bodyToMono(EmployeeVO.class);

        employeeVOMono.publishOn(Schedulers.parallel()).subscribe(employeeVO -> {
            Assertions.assertNotNull(employeeVO);
            log.info(employeeVO.toString());
            Assertions.assertTrue(employeeVO.getName().equalsIgnoreCase("TestName"));
            Assertions.assertTrue(employeeVO.getEmail().equalsIgnoreCase("test@gmail.com"));
            Assertions.assertNotNull(employeeVO.getId());
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    @Test
    public void testDeleteEmployee() throws InterruptedException {
        //First Save the Employee
        AtomicReference<UUID> uuid = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono<EmployeeVO> employeeVOMono = webClient.post().uri("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                        .fromValue(EmployeeVO
                                .builder()
                                .name("DeleteName")
                                .email("delete@gmail.com").build()
                        ))
                .retrieve()
                .bodyToMono(EmployeeVO.class);
        employeeVOMono.subscribe(employeeVO -> {
            Assertions.assertNotNull(employeeVO);
            uuid.set(employeeVO.getId());
            countDownLatch.countDown();
        });
        countDownLatch.await();
        //After Saving new Employee you can delete the same

        Mono<Void> deleteVoid = webClient.delete().uri("/employees/{employeeId}", uuid)
                .retrieve()
                .bodyToMono(Void.class);
        StepVerifier.create(deleteVoid).expectNextCount(0).verifyComplete();
    }

    @Test
    public void testUpdateEmployee() throws InterruptedException {
        //Save Employee
        AtomicReference<EmployeeVO> employeeVOAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Mono<EmployeeVO> employeeVOMono = webClient.post().uri("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                        .fromValue(EmployeeVO
                                .builder()
                                .name("InitialName")
                                .email("initial@gmail.com").build()
                        ))
                .retrieve()
                .bodyToMono(EmployeeVO.class);

        employeeVOMono.subscribe(employeeVO -> {
            employeeVOAtomicReference.set(employeeVO);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        //Update Employee
        EmployeeVO updatingEmployeeVO = EmployeeVO.builder().name("InitialName Updated").email("initialupdate@gmail.com").build();

        Mono<EmployeeVO> updatedEmployeeVOMono = webClient.put().uri("/employees/{employeeId}", employeeVOAtomicReference.get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                        .fromValue(updatingEmployeeVO))
                .retrieve()
                .bodyToMono(EmployeeVO.class);
        updatedEmployeeVOMono.subscribe(empVO -> {
            Assertions.assertNotNull(empVO);
            Assertions.assertTrue(empVO.getName().equalsIgnoreCase("InitialName Updated"));
            Assertions.assertTrue(empVO.getEmail().equalsIgnoreCase("initialupdate@gmail.com"));
            Assertions.assertEquals(empVO.getId(), employeeVOAtomicReference.get().getId());
            countDownLatch.countDown();
        });
        countDownLatch.await();

    }

    @Test
    public void testGetEmployeesPaginationDefault() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeePageList> employeePageListAtomicReference = new AtomicReference<>();
        Mono<EmployeePageList> employeeVOFlux = webClient.get().uri("/employees/pagination").accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(EmployeePageList.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeePageList -> {
            employeePageListAtomicReference.set(employeePageList);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        Assertions.assertEquals(employeePageListAtomicReference.get().stream().count(), 10);
    }

    @Test
    public void testGetEmployeesPaginationWithCustomPageSizeAndNumber() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeePageList> employeePageListAtomicReference = new AtomicReference<>();
        Mono<EmployeePageList> employeeVOFlux = webClient.get().uri(uriBuilder -> uriBuilder.path("/employees/pagination")
                .queryParamIfPresent("pageNumber", Optional.of(2))
                .queryParamIfPresent("pageSize", Optional.of(20))
                .build()).accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(EmployeePageList.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeePageList -> {
            employeePageListAtomicReference.set(employeePageList);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        Assertions.assertEquals(employeePageListAtomicReference.get().stream().count(), 20);
    }

    @Test
    public void testGetEmployeesPaginationWithEmployeeName() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeePageList> employeePageListAtomicReference = new AtomicReference<>();
        Mono<EmployeePageList> employeeVOFlux = webClient.get().uri(uriBuilder -> uriBuilder.path("/employees/pagination").queryParamIfPresent("name", Optional.of("Amsidh Lokhande")).build()).accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(EmployeePageList.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeePageList -> {
            employeePageListAtomicReference.set(employeePageList);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        Assertions.assertEquals(employeePageListAtomicReference.get().stream().count(), 10);

    }

    @Test
    public void testGetEmployeesPaginationWithEmployeeEmail() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeePageList> employeePageListAtomicReference = new AtomicReference<>();
        Mono<EmployeePageList> employeeVOFlux = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/employees/pagination")
                .queryParamIfPresent("email", Optional.of("amsidh@gmail.com")).build()).accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(EmployeePageList.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeePageList -> {
            employeePageListAtomicReference.set(employeePageList);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        Assertions.assertEquals(employeePageListAtomicReference.get().stream().count(), 10);

    }

    @Test
    public void testGetEmployeesPaginationWithEmployeeNameAndEmail() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<EmployeePageList> employeePageListAtomicReference = new AtomicReference<>();
        Mono<EmployeePageList> employeeVOFlux = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/employees/pagination")
                .queryParamIfPresent("name", Optional.of("Amsidh Lokhande"))
                .queryParamIfPresent("email", Optional.of("amsidh@gmail.com"))
                .build()).accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(EmployeePageList.class);
        employeeVOFlux.publishOn(Schedulers.parallel()).subscribe(employeePageList -> {
            employeePageListAtomicReference.set(employeePageList);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        Assertions.assertEquals(employeePageListAtomicReference.get().stream().count(), 10);

    }
}
