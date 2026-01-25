package com.example.application.views.pages;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.forms.RegisterForm;
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
public class RegistrationView extends DefaultPage implements HasNotifications {

	private static final Logger log = LoggerFactory.getLogger(RegistrationView.class);

	private final Binder<RegisterUserRequest> binder = new Binder<>(RegisterUserRequest.class);
	private final RegisterForm registerForm = new RegisterForm();

	@Autowired
	private InstrumentsFacadeService instrumentsFacadeService;

	public RegistrationView() {
		initBinder();
		initContent();
	}

	public void initContent() {
		log.info("Accessed registration page");
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
		registerForm.getSubmitBtn().addClickListener(l -> {
			if (binder.validate().isOk()) {
				RegisterUserRequest request = binder.getBean();
				instrumentsFacadeService.createNewUser(request);
				log.info("User '{}' created successfully", request.getUsername());
				showSuccessfulNotification("User created successfully!");
				getUI().ifPresent(ui -> ui.navigate(LoginView.class));
			} else {
				log.error("Submitted Registration Form with validation errors");
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
		binder.setBean(new RegisterUserRequest());
		binder.forField(registerForm.getUsername())
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() > 3, "Username must contain at least 4 characters")
				.withValidator(s -> s.length() < 12, "Username must contain less than 12 characters")
				.withValidator(s -> !instrumentsFacadeService.isUsernameTaken(s), "Username already exists")
				.bind(RegisterUserRequest::getUsername, RegisterUserRequest::setUsername);

		binder.forField(registerForm.getPassword())
				.asRequired("Please fill this field")
				.withValidator(t -> t.length() > 3, "Password must contain at least 4 characters")
				.withValidator(s -> s.length() < 20, "Password must contain less than 20 characters")
				.bind(RegisterUserRequest::getPassword, RegisterUserRequest::setPassword);

		binder.forField(registerForm.getConfirmPassword())
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() > 3, "Password must contain at least 4 characters")
				.withValidator(s -> s.equals(registerForm.getPassword().getValue()), "Passwords don't match")
				.bind(RegisterUserRequest::getConfirmPassword, RegisterUserRequest::setConfirmPassword);

		binder.forField(registerForm.getEmail())
				.asRequired("Please fill this field")
				.withValidator(new EmailValidator("Please enter a valid email address"))
				.withValidator(s -> !instrumentsFacadeService.isEmailTaken(s), "This email is already used")
				.withValidator(s -> s.length() < 30, "Email must contain less than 30 characters")
				.bind(RegisterUserRequest::getEmail, RegisterUserRequest::setEmail);
	}

}