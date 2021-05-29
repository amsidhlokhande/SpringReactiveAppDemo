package com.amsidh.mvc.springreactiveapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeVO {
    private Long id;
    private String name;
    private String email;
}
