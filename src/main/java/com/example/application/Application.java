package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of the Spring Boot application
 */
@SpringBootApplication
@Theme(value = "mytodo")
public class Application implements AppShellConfigurator {

    //https://www.youtube.com/watch?v=bxy2JgqqKDU&t=1428s
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
