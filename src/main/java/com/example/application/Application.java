package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * The entry point of the Spring Boot application
 */
@SpringBootApplication
@Theme(value = "light_theme")
@ComponentScan(basePackages = {
        "com.example.application",
        "com.example.application.security",
        "com.example.application.service"
})
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    // https://github.com/FlameXander/security-jwt
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addLink("shortcut icon", "icons/icon.png");
        settings.addFavIcon("icon", "icons/icon.png", "48x48");
    }

}
