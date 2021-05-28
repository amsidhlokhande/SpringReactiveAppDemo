package com.amsidh.mvc.springreactiveapp.repository;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, UUID> {
}
