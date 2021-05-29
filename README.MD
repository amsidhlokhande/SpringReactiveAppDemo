This application uses R2DBC with Reactive support.
List Of Tools/Lib/Functionality
1) Spring Reactive
2) R2DBC
3) Actuator
4) Swagger
5) H2 Database
6) UUID
7) Spring Security
8) Transaction
9) Exception Handling using @RestControllerAdvice
10) ConnectionFactoryInitializer


## EmployeeController.Java
```
package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public Flux<EmployeeVO> findAllEmployees() {
        return employeeService.getEmployees();
    }

    @GetMapping("/{employeeId}")
    public Mono<EmployeeVO> findEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return employeeService.getEmployee(employeeId);
    }

    @PostMapping
    public Mono<EmployeeVO> saveEmployee(@RequestBody EmployeeVO employeeVO) {
        return employeeService.createEmployee(employeeVO);
    }

    @PutMapping("/{employeeId}")
    public Mono<EmployeeVO> updateEmployeeById(@PathVariable("employeeId") UUID employeeId, @RequestBody Mono<EmployeeVO> monoEmployeeVO) {
        return employeeService.updateEmployee(employeeId, monoEmployeeVO);
    }

    @DeleteMapping("/{employeeId}")
    public Mono<Void> deleteEmployeeById(@PathVariable("employeeId") UUID employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

}

```
## Employee.Java
```
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

```
## EmployeeServiceImpl
```
package com.amsidh.mvc.springreactiveapp.service.impl;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;
import com.amsidh.mvc.springreactiveapp.repository.EmployeeRepository;
import com.amsidh.mvc.springreactiveapp.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<EmployeeVO> createEmployee(EmployeeVO employeeVO) {
        Employee employee = Optional.ofNullable(employeeVO)
                .map(empVo -> Employee.builder().name(empVo.getName()).email(empVo.getEmail()).build()).get();
        return employeeRepository.save(employee)
                .map(emp -> objectMapper.convertValue(emp, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> getEmployee(UUID id) {
        return employeeRepository.findById(id).map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));
    }

    @Override
    public Mono<EmployeeVO> updateEmployee(UUID id, Mono<EmployeeVO> monoEmployeeVO) {
        return this.employeeRepository.findById(id)
                .flatMap(employee -> monoEmployeeVO.map(inputEmployee -> {
                    Optional.ofNullable(inputEmployee.getName()).ifPresent(employee::setName);
                    Optional.ofNullable(inputEmployee.getEmail()).ifPresent(employee::setEmail);
                    return employee;
                })).flatMap(this.employeeRepository::save)
                .map(updatedEmployee -> objectMapper.convertValue(updatedEmployee, EmployeeVO.class));

    }

    @Override
    public Mono<Void> deleteEmployee(UUID id) {
        return employeeRepository.deleteById(id);
    }

    @Override
    public Flux<EmployeeVO> getEmployees() {
        return employeeRepository.findAll().map(employee -> objectMapper.convertValue(employee, EmployeeVO.class));

    }
}

```

## EmployeeRepository
```
 package com.amsidh.mvc.springreactiveapp.repository;

import com.amsidh.mvc.springreactiveapp.entity.Employee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EmployeeRepository extends ReactiveCrudRepository<Employee, UUID> {

    @Query("SELECT * FROM employee WHERE name = :name")
    Flux<Employee> findByName(String name);
}


```

## AppConfig
```
  package com.amsidh.mvc.springreactiveapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;


@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ConnectionFactoryInitializer getConnectionFactoryInitializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer connectionFactoryInitializer = new ConnectionFactoryInitializer();
        connectionFactoryInitializer.setConnectionFactory(connectionFactory);


        connectionFactoryInitializer.setDatabasePopulator(
                new ResourceDatabasePopulator(new ClassPathResource("schema.sql"),
                        new ClassPathResource("data.sql")));

        return connectionFactoryInitializer;
    }

}

```

## SpringSecurityConfig.java
```
  package com.amsidh.mvc.springreactiveapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityWebFilterChain getSecurityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET).permitAll()
                .pathMatchers("/h2-console/**", "/actuator/**", "/webjars/swagger-ui/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable().build();
    }

}

```
## EmployeeControllerAdvice.java
```
package com.amsidh.mvc.springreactiveapp.controller;

import com.amsidh.mvc.springreactiveapp.exception.EmployeeNotFoundException;
import com.amsidh.mvc.springreactiveapp.exception.EmployeeUpdateException;
import com.amsidh.mvc.springreactiveapp.exception.NoDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class EmployeeControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverExceptionHandler(Exception ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String employeeNotFoundException(EmployeeNotFoundException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NoDataFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String employeeNotDataFoundException(NoDataFoundException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    @ExceptionHandler(EmployeeUpdateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String employeeNotDataFoundException(EmployeeUpdateException ex) {
        logToConsole(ex);
        return ex.getMessage();
    }

    private void logToConsole(Exception ex) {
        if (log.isDebugEnabled()) {
            log.error(ex.getMessage(), ex);
        } else {
            log.error(ex.getLocalizedMessage());
        }
    }
}

```

## DataLoader.java
```
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

```

## EmployeeVO.java

```
package com.amsidh.mvc.springreactiveapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeVO {
    private UUID id;
    private String name;
    private String email;
}

```

## EmployeeNotFoundException.java
```
package com.amsidh.mvc.springreactiveapp.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(UUID id) {
        super(String.format("Employee with EmployeeId %s not found", id));
    }
}

```
## EmployeeUpdateException.java
```
package com.amsidh.mvc.springreactiveapp.exception;

import com.amsidh.mvc.springreactiveapp.model.EmployeeVO;

import java.util.UUID;

public class EmployeeUpdateException extends RuntimeException {

    public EmployeeUpdateException(UUID id, EmployeeVO employeeVO) {
        super(String.format("Update employee with employee %s failed. Employee Details are %s", id, employeeVO.toString()));
    }
}

```

## NoDataFoundException.java
```
package com.amsidh.mvc.springreactiveapp.exception;

public class NoDataFoundException extends RuntimeException {

    public NoDataFoundException() {
        super("No data found");
    }
}
```

## SpringReactiveAppApplication.java

```
package com.amsidh.mvc.springreactiveapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringReactiveAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringReactiveAppApplication.class, args);
    }

}

```

## application.properties 
```
#Spring App Configuration
server.port=8181

#RDBMS Database Configuration
#spring.datasource.url=jdbc:h2:mem:employeedb
#spring.datasource.username=test
#spring.datasource.password=test
#spring.jpa.hibernate.ddl-auto=create

#R2DBC Database Configuration
spring.r2dbc.url=r2dbc:h2:mem:///employeedb
#spring.r2dbc.url=r2dbc:h2:file//my/relative/path
spring.r2dbc.username=user
spring.r2dbc.password=password
spring.r2dbc.pool.enabled=true


#H2 Console Config
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true

#Spring Security Config
spring.security.user.name=user
spring.security.user.password=password

#Logging
logging.level.root=info
logging.level.org.springframework.r2dbc=DEBUG

#Actuator endpoint configuration
management.endpoints.web.exposure.include=*

```
## data.sql
```
DELETE FROM EMPLOYEE;
INSERT INTO  EMPLOYEE (name, email) VALUES ('Amsidh Lokhande', 'amsidhlokhande@gmail.com');
INSERT INTO  EMPLOYEE (name, email) VALUES ('Anjali Lokhande', 'anjalilokhande@gmail.com');
INSERT INTO  EMPLOYEE (name, email) VALUES ('Adithi Lokhande', 'adithilokhande@gmail.com');
INSERT INTO  EMPLOYEE (name, email) VALUES ('Aditya Lokhande', 'adityalokhande@gmail.com');
INSERT INTO  EMPLOYEE (name, email) VALUES ('Raju Lokhande', 'rajulokhande@gmail.com');
INSERT INTO  EMPLOYEE (name, email) VALUES ('Suresh Rupnar', 'sureshrupnar@gmail.com');
```
## schema.sql
```
CREATE TABLE IF NOT EXISTS EMPLOYEE
(
    id UUID default random_uuid(),
    name  VARCHAR(255),
    email VARCHAR(255),
    PRIMARY KEY (id)
);

```
## pom.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <!--<version>2.5.0</version>-->
        <version>2.4.6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.amsidh.mvc</groupId>
    <artifactId>SpringReactiveApp</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>SpringReactiveApp</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>11</java.version>
        <springdoc.version>1.5.2</springdoc.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-r2dbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.r2dbc</groupId>
            <artifactId>r2dbc-h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-webflux-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```


## Swagger API Urls:
Swagger-> http://localhost:8181/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/employee-controller/findEmployeeById

## GET ALL Employees
http://localhost:8181/employees
#GET EMPLOYEE BY ID
http://localhost:8181/employees/37ce2e97-5fd0-4def-a946-ae1398c4d6de
## ACTUATOR
http://localhost:8181/actuator
