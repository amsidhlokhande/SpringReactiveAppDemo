package com.amsidh.mvc.springreactiveapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee implements Persistable<Long> {

    @Id
    private Long id;
    private String name;
    private String email;

    @Transient
    @JsonIgnore
    private boolean newEmployee;

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public boolean isNew() {
        return this.newEmployee || this.id == null;
    }

    public Employee setAsNew() {
        this.newEmployee = true;
        return this;
    }

}
