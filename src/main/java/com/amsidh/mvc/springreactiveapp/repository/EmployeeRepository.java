package com.amsidh.mvc.springreactiveapp.repository;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    @Query("SELECT * FROM employee WHERE name = :name")
    Flux<Employee> findByName(String name);
}
