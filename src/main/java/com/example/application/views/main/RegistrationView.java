package com.example.application.views.main;

import com.example.application.entities.User;
import com.example.application.services.UserService;
import com.example.application.views.main.components.RegisterComponent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@PageTitle("Registation")
@Route(value = "register")
public class RegistrationView extends Main {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationView.class);

    private final RegisterComponent registerComponent;

    private final UserService userService;

    private final Binder<User> binder;

    public RegistrationView(UserService userService) {
        this.userService = userService;

        registerComponent = new RegisterComponent();
        binder = new Binder<>(User.class);

        RegisterComponent registration = new RegisterComponent();
        add(registration);

        initBinder();
        initContent();
    }

    public void initContent() {
        logger.info("Accessed registration page");
        addClassName("page-content");

        H2 title = new H2("Register");

        registerComponent.getSubmitButton().addClickListener(e -> {
            if (binder.validate().isOk()) {
                User user = binder.getBean();
                logger.info("User '{}' created succesfully", user.getUsername());
                userService.createNewUser(user);
                showSuccesfullNotification();
//                UI.getCurrent().navigate(LoginView.class);
            } else {
                logger.warn("Submitted Registration Form with validation errors");
                showErrorNotification();
            }
        });

    }

    private void initBinder() {
        binder.setBean(new User());
        binder.forField(registerComponent.getUsernameField())
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Username must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Username must contain less than 12 characters")
                .withValidator(s -> userService.findByUsernameIgnoreCase(s).isEmpty(), "Username already exists")
                .bind(User::getUsername, User::setUsername);

        binder.forField(registerComponent.getPasswordField())
                .asRequired("Please fill this field")
                .withValidator(t -> t.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Password must contain less than 12 characters");

        binder.forField(registerComponent.getConfirmPasswordField())
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.equals(registerComponent.getPasswordField().getValue()), "Passwords don't match")
                .bind(User::getPassword, User::setPassword);

        binder.forField(registerComponent.getEmailField())
                .asRequired("Please fill this field")
                .withValidator(new EmailValidator("Please enter a valid email address"))
                .withValidator(s -> userService.findByEmailIgnoreCase(s).isEmpty(), "This email is already used")
                .bind(User::getEmail, User::setEmail);
    }

    private void showSuccesfullNotification() {
        Notification notification = Notification.show("User created succesfully!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

    private void showErrorNotification() {
        Notification notification = Notification.show("An error occured while submiting Registration Form");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.setDuration(5000);
        notification.open();
    }

}


