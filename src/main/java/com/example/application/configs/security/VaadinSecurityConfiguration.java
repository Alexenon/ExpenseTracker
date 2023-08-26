package com.example.application.configs.security;

import com.example.application.utils.JwtTokenUtils;
import com.example.application.views.main.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Order(2)
@Configuration
@EnableWebSecurity
public class VaadinSecurityConfiguration extends VaadinWebSecurity {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/public/**").permitAll();
                    auth.requestMatchers("/icons/**").permitAll();
                    auth.requestMatchers("/images/**").permitAll();
                });

        super.configure(http);
        setLoginView(http, LoginView.class);

//        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(jwtTokenUtils.getSecret()), JwsAlgorithms.HS256);
//        setStatelessAuthentication(http, secretKey, "com.example.application", jwtTokenUtils.getJwtLifetime().toMillis());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
