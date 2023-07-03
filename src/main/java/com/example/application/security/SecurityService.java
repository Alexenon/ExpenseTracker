package com.example.application.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public record SecurityService(AuthenticationContext authenticationContext) {

    // TODO: Add AuthenticationException
    public UserDetails getAuthenticatedUser() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).orElseThrow();
    }

    public void logout() {
        authenticationContext.logout();
    }
}