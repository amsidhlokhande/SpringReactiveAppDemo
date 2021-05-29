package com.amsidh.mvc.springreactiveapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {

    @Id
    private UUID id;
    private String name;
    private String email;

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
