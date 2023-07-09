package com.example.application.configs;

import com.example.application.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**").permitAll();
                    auth.requestMatchers("/vaadinServlet/UIDL/**").permitAll();
                    auth.requestMatchers("/vaadinServlet/HEARTBEAT/**").permitAll();
                    auth.requestMatchers("/resources/**").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/api/**").authenticated();
                    auth.requestMatchers("/secured").authenticated();
                    auth.requestMatchers("/admin").hasRole("ADMIN");
//                    auth.anyRequest().authenticated();
                })
                .formLogin(loginForm -> {
                    loginForm.loginPage(LOGIN_URL);
                    loginForm.loginProcessingUrl(LOGIN_PROCESSING_URL);
                    // loginForm.successForwardUrl(LOGIN_SUCCESS_URL);
                    loginForm.failureUrl(LOGIN_FAILURE_URL);
                })
                .logout(logout -> logout.logoutSuccessUrl(LOGOUT_SUCCESS_URL))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .rememberMe().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        //.and().addFilterBefore()...

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/resources/**")
                .requestMatchers("/VAADIN/**")
                .requestMatchers("/PUSH/**")
                .requestMatchers("/UIDL/**")
                .requestMatchers("/dev-bundle/**")
                .requestMatchers("/vaadinServlet/**")
                .requestMatchers("/chromewebdata/**")
                .requestMatchers("/images/**")
                .requestMatchers("/icons/**");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
