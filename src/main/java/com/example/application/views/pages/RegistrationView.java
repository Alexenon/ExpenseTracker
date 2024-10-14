package com.example.application.views.pages;

import com.example.application.entities.User;
import com.example.application.services.UserService;
import com.example.application.views.components.complex_components.forms.RegisterForm;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Registration")
@Route(value = "register")
@CssImport("./themes/light_theme/styles/page-styles/auth-pages.css")
public class RegistrationView extends Main implements HasNotifications {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationView.class);

    private final Binder<User> binder;
    private final RegisterForm registerForm;

    @Autowired
    private UserService userService;

    public RegistrationView() {
        binder = new Binder<>(User.class);
        registerForm = new RegisterForm();

        initBinder();
        initContent();
    }

    public void initContent() {
        logger.info("Accessed registration page");
        setId("register-page");

        H2 title = new H2("Register");
        title.addClassName("form-title");
        registerForm.setId("register-form");
        registerForm.addComponentAsFirst(title);
        registerForm.getSubmitBtn().addClassName("submit-btn");
        addSubmitListener();

        Div registerContainer = new Div(registerForm);
        registerContainer.addClassName("register-container");

        add(registerContainer, getPanelContainer());
    }

    private void addSubmitListener() {
        registerForm.getSubmitBtn().addClickListener(e -> {
            logger.info("Clicked");
            if (binder.validate().isOk()) {
                showSuccessfulNotification("User created successfully!");
                User user = binder.getBean();
                logger.info("User '{}' created successfully", user.getUsername());
                userService.createNewUser(user);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            } else {
                logger.warn("Submitted Registration Form with validation errors");
                showErrorNotification("Submitted Registration Form with validation errors");
            }
        });
    }

    private Div getPanelContainer() {
        H3 h3 = new H3("Welcome Back!");
        Paragraph p = new Paragraph("To keep connected with us please login with your personal info");
        Button btn = new Button("Log in", e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        Image image = new Image("images/forgot.svg", "Register page background image");

        btn.addClassNames("btn", "transparent");
        image.addClassName("image");

        Div content = new Div(h3, p, btn);
        content.addClassNames("content");

        Div rightPanel = new Div(content, image);
        rightPanel.addClassNames("panel", "right-panel");

        Div panelsContainer = new Div(rightPanel);
        panelsContainer.addClassNames("panels-container", "position-right");

        return panelsContainer;
    }

    private void initBinder() {
        binder.setBean(new User());
        binder.forField(registerForm.getUsername())
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Username must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Username must contain less than 12 characters")
                .withValidator(s -> !userService.checkIfUsernameExists(s), "Username already exists")
                .bind(User::getUsername, User::setUsername);

        binder.forField(registerForm.getPassword())
                .asRequired("Please fill this field")
                .withValidator(t -> t.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.length() < 12, "Password must contain less than 12 characters")
                .bind(User::getPassword, User::setPassword);

        binder.forField(registerForm.getConfirmPassword())
                .asRequired("Please fill this field")
                .withValidator(s -> s.length() > 3, "Password must contain at least 4 characters")
                .withValidator(s -> s.equals(registerForm.getPassword().getValue()), "Passwords don't match")
                .bind(User::getPassword, User::setPassword);

        binder.forField(registerForm.getEmail())
                .asRequired("Please fill this field")
                .withValidator(new EmailValidator("Please enter a valid email address"))
                .withValidator(s -> !userService.checkIfEmailExists(s), "This email is already used")
                .bind(User::getEmail, User::setEmail);
    }

}


