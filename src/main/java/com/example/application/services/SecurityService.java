package com.example.application.services;

import com.example.application.entities.User;
import com.example.application.utils.exceptions.UnauthenticatedUserException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        Object principal = context.getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of((UserDetails) context.getAuthentication().getPrincipal());
        }

        return Optional.empty();
    }

    public User getAuthenticatedUser() {
        String username = getAuthenticatedUserDetails()
                .orElseThrow(() -> new UnauthenticatedUserException("User is not authenticated. " +
                                                                    "Please log in to access this resource."))
                .getUsername();

        return userService.findByUsername(username);
    }

    public void logout() {
        logger.info("User logged out");
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
