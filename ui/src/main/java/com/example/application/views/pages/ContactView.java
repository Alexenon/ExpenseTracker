package com.example.application.views.pages;

import com.example.application.data.requests.RegisterUserRequest;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.forms.ContactUsForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@AnonymousAllowed
@PageTitle("Contact")
@Route(value = "contact")
@CssImport("./themes/light_theme/styles/page-styles/auth-pages.css")
public class ContactView extends DefaultPage {

	private final TextField name = new TextField("Name");
	private final EmailField email = new EmailField("Email");
	private final TextArea message = new TextArea("Message");
	private final Button sendBtn = new Button("Send");
	private final Image image = new Image("images/envelope.svg", "Contact image");

	private final Binder<RegisterUserRequest> binder = new Binder<>(RegisterUserRequest.class);
	private final ContactUsForm contactUsForm = new ContactUsForm();

	@Autowired
	private InstrumentsFacadeService instrumentsFacadeService;

	public ContactView() {
		initBinder();
		initContent();
	}

	public void initContent() {
		setId("register-page");

		H2 title = new H2("Contact us");
		title.addClassName("form-title");
		contactUsForm.setId("register-form");
		contactUsForm.addComponentAsFirst(title);
		contactUsForm.getSendBtn().addClassName("submit-btn");
		addSubmitListener();

		Div registerContainer = new Div(contactUsForm);
		registerContainer.addClassName("register-container");

		add(registerContainer, getRightPanel());
	}

	private void addSubmitListener() {
		contactUsForm.getSendBtn().addClickListener(l -> {
			if (binder.validate().isOk()) {
				RegisterUserRequest request = binder.getBean();
				instrumentsFacadeService.createNewUser(request);
				showSuccessfulNotification("User created successfully!");
				getUI().ifPresent(ui -> ui.navigate(LoginView.class));
			} else {
				showErrorNotification("Submitted 'Contact us' form with validation errors");
			}
		});
	}

	private Div getRightPanel() {
		Image image = new Image("images/envelope.svg", "Contact us image");
		image.addClassName("image");
		image.getStyle().set("width", "70vh");

		Div rightPanel = new Div(image);
		rightPanel.addClassNames("panel", "right-panel");

		Div panelsContainer = new Div(rightPanel);
		panelsContainer.addClassNames("panels-container", "position-right");

		return panelsContainer;
	}

	private void initBinder() {
		binder.setBean(new RegisterUserRequest());
		binder.forField(contactUsForm.getName())
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() >= 4, "Name must contain at least 4 characters")
				.withValidator(s -> s.length() <= 255, "Name must not exceed 255 characters")
				.withValidator(s -> instrumentsFacadeService.isUsernameAvailable(s), "Username already exists")
				.bind(RegisterUserRequest::getUsername, RegisterUserRequest::setUsername);

		binder.forField(contactUsForm.getMessage())
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() <= 1000, "Message must contain less than 1000 characters")
				.bind(RegisterUserRequest::getPassword, RegisterUserRequest::setPassword);

		binder.forField(contactUsForm.getEmail())
				.asRequired("Please fill this field")
				.withValidator(new EmailValidator("Please enter a valid email address"))
				.withValidator(s -> instrumentsFacadeService.isEmailAvailable(s), "This email is already used")
				.withValidator(s -> s.length() <= 320, "Email must must not exceed 320 characters")
				.bind(RegisterUserRequest::getEmail, RegisterUserRequest::setEmail);
	}

}
