package com.instantsystem.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableMongoRepositories
@EnableWebMvc
public class InstantSystemDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstantSystemDemoApplication.class, args);
    }

}
