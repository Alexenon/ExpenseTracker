package com.example.application.views.main;

import com.example.application.dtos.RegistrationUserDTO;
import com.example.application.services.UserService;
import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Registation")
@Route(value = "register", layout = MainLayout.class)
public class RegistrationView extends Main {

    private final UserService userService;

    TextField username = new TextField("Username");
    PasswordField password1 = new PasswordField("Password");
    PasswordField password2 = new PasswordField("Confirm password");
    EmailField email = new EmailField("Email");

    public RegistrationView(UserService userService) {
        this.userService = userService;
        initContent();
    }

    public void initContent() {
        addClassName("page-content");
        H2 title = new H2("Register");
        Button button = new Button("Send", e -> {
            if (isValid()) {
                userService.createNewUser(getRegistrationUserDTO());
            }
        });
        VerticalLayout layout = new VerticalLayout(title, username, password1, password2, email, button);
        add(layout);
    }

    private RegistrationUserDTO getRegistrationUserDTO() {
        return new RegistrationUserDTO() {
            @Override
            public String getUsername() {
                return username.getValue();
            }

            @Override
            public String getPassword() {
                return password1.getValue();
            }

            @Override
            public String getConfirmPassword() {
                return password2.getValue();
            }

            @Override
            public String getEmail() {
                return email.getValue();
            }
        };
    }

    private boolean isValid() {
        if (username.getValue().trim().isEmpty()) {
            Notification.show("Enter a username");
            return false;
        } else if (password1.isEmpty()) {
            Notification.show("Enter a password");
            return false;
        } else if (!password1.getValue().equals(password2.getValue())) {
            Notification.show("Passwords don't match");
            return false;
        }

        return true;
    }


}


