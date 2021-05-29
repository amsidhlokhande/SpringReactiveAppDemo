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
        employeeRepository.findAll().doOnNext(employee -> log.info(employee.toString())).blockLast(Duration.ofSeconds(10));

        log.info("");

        // fetch Employees by last name
        log.info("Employees found with findByName('Amsidh Lokhande100'):");
        log.info("--------------------------------------------");
        employeeRepository.findByName("Test User1").doOnNext(amsidh -> log.info(amsidh.toString())).blockLast(Duration.ofSeconds(10));

        log.info("");
    }

    private List<Employee> getInitialEmployeeData() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            employeeList.add(new Employee("Test User" + i, "testuser@gmail.com" + i));
        }
        return employeeList;
    }

}
