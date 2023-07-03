package com.example.application.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public record SecurityService(AuthenticationContext authenticationContext) {

    //    // TODO: Add AuthenticationException
//    public UserDetails getAuthenticatedUser() {
//        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElseThrow();
//    }

    public UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) context.getAuthentication().getPrincipal();
        }

        return null;
    }

    public void logout() {
        authenticationContext.logout();
    }
}