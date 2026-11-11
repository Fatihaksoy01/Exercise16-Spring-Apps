package pl.edu.vistula.firstrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "First REST API - Products",
        version = "1.0",
        description = "Exercise 16: product CRUD API with Spring Data JPA and H2"))
public class FirstRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstRestApiApplication.class, args);
    }

}
