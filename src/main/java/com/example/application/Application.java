package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    // https://github.com/ECharts-Java/ECharts-Java
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}


