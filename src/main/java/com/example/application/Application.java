package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application
 */
@SpringBootApplication
@Theme(value = "light_theme")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // TODO: Move this into AppConfig
    //  Rename AppConfig after
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addLink("shortcut icon", "icons/icon.png");
        settings.addFavIcon("icon", "icons/icon.png", "48x48");
    }

}


