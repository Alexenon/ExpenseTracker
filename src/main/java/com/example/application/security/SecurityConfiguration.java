package com.example.application.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableWebSecurity
//@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String DUMMY_LOGIN = "admin";

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGOUT_SUCCESS_URL = "/login";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().antMatcher("/forgotpassword**")
                .requestCache().requestCache(new CustomRequestCache())
                .and()
                .authorizeRequests()
                .antMatchers("/forgotpassword").permitAll()
                .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
                .antMatchers("/users/**").hasAnyAuthority(UserRole.SUPER_ADMIN.name(), UserRole.ADMIN.name())
                .antMatchers("/companies/**").hasAuthority(UserRole.SUPER_ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(LOGIN_URL).permitAll()
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }


    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",
                "/icons/**",
                "/images/**",
                "/styles/**",
                "/h2-console/**");
    }

    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CurrentLogin currentLogin(UserRepository userRepository) {
        final String username = SecurityUtils.getUsername();
        if (DUMMY_LOGIN.equals(username)) {
            return dummyCurrentLogin.get();
        }
        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(username);
        if (optionalUser.isPresent()) {
            Optional<Company> optionalCompany = companyService.findById(optionalUser.get().getCompanyId());
            if (optionalCompany.isPresent()) {
                return new CurrentLoginImpl(optionalUser.get(), optionalCompany.get());
            }
        }
        return null;
    }

}