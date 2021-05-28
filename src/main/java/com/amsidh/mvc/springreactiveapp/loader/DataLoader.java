package com.amsidh.mvc.springreactiveapp.loader;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {

        if (StreamSupport.stream(employeeRepository.findAll().spliterator(), true).count() == 0) {
            employeeRepository.saveAll(getInitialEmployeeData())
                    .forEach(System.out::println);
        }
    }

    private List<Employee> getInitialEmployeeData() {
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            employeeList.add(new Employee("Amsidh Lokhande" + i, "amsidhlokhande@gmail.com" + i));
        }
        return employeeList;
    }

}
