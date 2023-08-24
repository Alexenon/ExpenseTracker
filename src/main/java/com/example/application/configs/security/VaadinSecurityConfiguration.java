package com.example.application.configs.security;

import com.example.application.utils.JwtTokenUtils;
import com.example.application.views.main.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Order(1)
@Configuration
@EnableWebSecurity
public class VaadinSecurityConfiguration extends VaadinWebSecurity {

    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGOUT_SUCCESS_URL = "/";
    private static final String DENIED_PAGE_URL = "/404";

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/register").permitAll();
                    auth.requestMatchers("/public/**").permitAll();
                    auth.requestMatchers("/icons/**").permitAll();
                    auth.requestMatchers("/images/**").permitAll();
                })
                .formLogin(loginForm -> {
                    loginForm.loginPage(LOGIN_URL);
                    loginForm.loginProcessingUrl(LOGIN_PROCESSING_URL);
                    loginForm.failureUrl(LOGIN_FAILURE_URL);
                })
                .logout(logout -> logout.logoutSuccessUrl(LOGOUT_SUCCESS_URL))
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
                    e.accessDeniedPage(DENIED_PAGE_URL);
                });

        super.configure(http);
        setLoginView(http, LoginView.class);

        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(jwtTokenUtils.getSecret()), JwsAlgorithms.HS256);
        setStatelessAuthentication(http, secretKey, "com.example.application", jwtTokenUtils.getJwtLifetime().toMillis());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
