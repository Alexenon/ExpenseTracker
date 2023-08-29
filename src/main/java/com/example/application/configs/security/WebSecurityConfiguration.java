//package com.example.application.configs.security;
//
//import com.example.application.services.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfiguration {
//
//    private static final String LOGIN_URL = "/login";
//    private static final String LOGIN_PROCESSING_URL = "/login";
//    private static final String LOGIN_FAILURE_URL = "/login?error";
//    private static final String LOGOUT_SUCCESS_URL = "/";
//    private static final String DENIED_PAGE_URL = "/404";
//
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtRequestFilter jwtRequestFilter;
//
//    @Bean
//    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf().disable()
//                .securityMatcher("/api/**")
//                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .httpBasic(Customizer.withDefaults())
//                .build();
//    }
//
//    @Bean
//    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf().disable()
//                .authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/").permitAll();
//                    auth.requestMatchers("/login").permitAll();
//                    auth.requestMatchers("/register").permitAll();
//                    auth.requestMatchers("/icons/**").permitAll();
//                    auth.requestMatchers("/images/**").permitAll();
//                    auth.requestMatchers("/VAADIN/**").permitAll();
//                    auth.anyRequest().authenticated();
//                })
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
//                .formLogin(loginForm -> {
//                    loginForm.loginPage(LOGIN_URL);
//                    loginForm.loginProcessingUrl(LOGIN_PROCESSING_URL);
//                    loginForm.failureUrl(LOGIN_FAILURE_URL);
//                })
//                .oauth2Login(oauth2Login -> oauth2Login.loginPage(LOGIN_URL).permitAll())
//                .logout(logout -> logout.logoutSuccessUrl(LOGOUT_SUCCESS_URL))
//                .exceptionHandling(e -> {
//                    e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
//                    e.accessDeniedPage(DENIED_PAGE_URL);
//                })
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
//        return authConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
//        daoAuthenticationProvider.setUserDetailsService(userService);
//        return daoAuthenticationProvider;
//    }
//
//}
