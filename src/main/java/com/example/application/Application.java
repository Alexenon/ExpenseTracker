package com.example.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The entry point of the Spring Boot application
 */
@EnableScheduling
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    // https://github.com/ECharts-Java/ECharts-Java
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}


