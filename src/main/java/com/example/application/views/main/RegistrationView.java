package com.example.application.views.main;

import com.example.application.entities.User;
import com.example.application.services.UserService;
import com.example.application.views.main.components.RegisterComponent;
import com.example.application.views.main.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
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
@PageTitle("Registation")
@Route(value = "register")
public class RegistrationView extends Main implements HasNotifications {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationView.class);

    private final Binder<User> binder;
    private final RegisterComponent registerComponent;

    @Autowired
    private UserService userService;

    public RegistrationView() {
        binder = new Binder<>(User.class);
        registerComponent = new RegisterComponent();

        initBinder();
        initContent();
    }

    public void initContent() {
        logger.info("Accessed registration page");
        addClassName("register-page");

        H2 title = new H2("Register");
        Div panelContainer = getPanelContainer();
        registerComponent.addClassName("signup-container");

        addSubmitListener();

        add(title, registerComponent, panelContainer);
    }

    private void addSubmitListener() {
        registerComponent.getSubmitButton().addClickListener(e -> {
            logger.info("Clicked");
            if (binder.validate().isOk()) {
                User user = binder.getBean();
                logger.info("User '{}' created succesfully", user.getUsername());
                userService.createNewUser(user);
                showSuccesfullNotification("User created succesfully!");
                // UI.getCurrent().navigate(LoginView.class);
            } else {
                logger.warn("Submitted Registration Form with validation errors");
                showErrorNotification("Submitted Registration Form with validation errors");
            }
        });
    }

    private Div getPanelContainer() {
        H3 h3 = new H3("New here ?");
        Paragraph p = new Paragraph("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Debitis, ex ratione. Aliquid!");
        Button btn = new Button("Sign up", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));
        Image image = new Image("images/logo_black.png", "Login panel image");

        btn.addClassNames("btn", "transparent");
        image.addClassName("image");

        Div content = new Div(h3, p, btn);
        content.addClassNames("content");

        Div leftPanel = new Div(content, image);
        leftPanel.addClassNames("panel", "left-panel");

        Div panelsContainer = new Div(leftPanel);
        panelsContainer.addClassNames("panels-container", "position-left");

        return panelsContainer;
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
                .withValidator(s -> s.length() < 12, "Password must contain less than 12 characters")
                .bind(User::getPassword, User::setPassword);

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

}


