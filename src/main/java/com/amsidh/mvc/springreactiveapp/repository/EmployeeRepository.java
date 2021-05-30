package com.amsidh.mvc.springreactiveapp.repository;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EmployeeRepository extends ReactiveSortingRepository<Employee, UUID> {

    @Query("SELECT * FROM employee WHERE name = :name")
    Flux<Employee> findByName(String name);

}
