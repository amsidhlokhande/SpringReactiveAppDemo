package com.amsidh.mvc.springreactiveapp.loader;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {

        // save a few customers
        employeeRepository.saveAll(getInitialEmployeeData())
                .blockLast(Duration.ofSeconds(10));

        // fetch all Employees
        log.info("Employees found with findAll():");
        log.info("-------------------------------");
        employeeRepository.findAll()
                .doOnNext(employee -> log.info(employee.toString()))
                .blockLast(Duration.ofSeconds(10));

        log.info("");

        // fetch Employees by last name
        log.info("Employees found with findByName('Amsidh Lokhande100'):");
        log.info("--------------------------------------------");
        employeeRepository.findByName("Test User1")
                .doOnNext(amsidh -> log.info(amsidh.toString()))
                .blockLast(Duration.ofSeconds(10));
    }

    private List<Employee> getInitialEmployeeData() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            if (i < 300) {
                employeeList.add(Employee.builder().name("Amsidh Lokhande").email("amsidh@gmail.com").build());
            } else if (300 < i && i < 500) {
                employeeList.add(Employee.builder().name("Anjali Lokhande").email("anjali@gmail.com").build());
            } else if (500 < i && i < 700) {
                employeeList.add(Employee.builder().name("Adithi Lokhande").email("adithi@gmail.com").build());
            } else {
                employeeList.add(Employee.builder().name("Aditya Lokhande").email("aditya@gmail.com").build());
            }

        }
        return employeeList;
    }

}
