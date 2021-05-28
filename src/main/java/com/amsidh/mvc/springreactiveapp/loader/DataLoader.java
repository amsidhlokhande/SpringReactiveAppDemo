package com.amsidh.mvc.springreactiveapp.loader;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.findAll().isEmpty()) {
            employeeRepository.saveAllAndFlush(() -> Arrays.asList(new Employee("Amsidh Lokhande", "amsidhlokhande@gmail.com"),
                    new Employee("Anjali Lokhande", "anjalilokhande@gmail.com"),
                    new Employee("Aditya Lokhande", "adityalokhande@gmail.com"),
                    new Employee("Adithi Lokhande", "adithilokhande@gmail.com"),
                    new Employee("Gaurav Rathi", "gauravrathi@gmail.com")).iterator())
                    .forEach(System.out::println);
        }
    }
}
