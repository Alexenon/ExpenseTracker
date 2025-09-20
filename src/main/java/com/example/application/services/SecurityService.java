package com.example.application.services;

import com.example.application.entities.User;
import com.example.application.utils.exceptions.auth.UnauthenticatedUserException;
import com.example.application.utils.exceptions.auth.UserNotFoundException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    private static final String LOGOUT_SUCCESS_URL = "/";

    private final UserService userService;

    @Autowired
    public SecurityService(UserService userService) {
        this.userService = userService;
    }

    public Optional<UserDetails> getAuthenticatedUserDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication == null)
            throw new UnauthenticatedUserException("Coudn't manage to receive authentication. Please re-login");

        return authentication.getPrincipal() instanceof UserDetails userDetails
                ? Optional.of(userDetails)
                : Optional.empty();
    }

    @NotNull
    public User getAuthenticatedUser() {
        String username = getAuthenticatedUserDetails()
                .orElseThrow(() -> new UnauthenticatedUserException("Unauthorized exception. Please log in."))
                .getUsername();

        return userService.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("There is no such user with username = %s".formatted(username)));
    }

    public boolean isCurrentUserAuthenticated() {
        return getAuthenticatedUserDetails().isPresent();
    }

    public void logout() {
        logger.info("User logged out");
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
