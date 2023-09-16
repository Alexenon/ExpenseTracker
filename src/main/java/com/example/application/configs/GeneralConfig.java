package com.example.application.configs;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Theme(value = "light_theme")
public class GeneralConfig implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addLink("shortcut icon", "icons/icon.png");
        settings.addFavIcon("icon", "icons/icon.png", "48x48");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DatePicker.DatePickerI18n singleFormatI18n() {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setFirstDayOfWeek(1); // Monday
        i18n.setDateFormat("yyyy-MM-dd");
        return i18n;
    }

}
