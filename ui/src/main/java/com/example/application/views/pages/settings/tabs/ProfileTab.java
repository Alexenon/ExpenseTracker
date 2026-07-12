package com.example.application.views.pages.settings.tabs;

import com.example.application.InstrumentsFacadeService;
import com.example.application.user.domain.UpdateUserRequest;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.utils.CommonButtons;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.Currency;

@Slf4j
public class ProfileTab extends AbstractSettingsTab implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField usernameField = new TextField("Username");
	private final EmailField emailField = new EmailField("Email");
	private final Select<Currency> currencyField = new Select<>();
	private final Button saveBtn = CommonButtons.createSaveButton("Save");

	private final Binder<UpdateUserRequest> binder = new Binder<>(UpdateUserRequest.class);

	public ProfileTab(InstrumentsFacadeService instrumentsFacadeService) {
		super("Profile", VaadinIcon.USER.create());
		this.instrumentsFacadeService = instrumentsFacadeService;
		initializeFields();
		initBinder();
		initListeners();

	}

	@Override
	public Div getContent() {
		Container fieldsContainer = Container.builder("fields-wrapper")
				.addComponents(usernameField, emailField, saveBtn)
				.build();

		return Container.builder()
				.addComponent(fieldsContainer)
				.build();
	}

	private void initializeFields() {
		currencyField.setItems(Currency.getAvailableCurrencies());
	}

	private void initBinder() {
		binder.setBean(getDefaultRequest());
		binder.forField(usernameField)
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() >= 4, "Name must contain at least 4 characters")
				.withValidator(s -> s.length() <= 255, "Name must not exceed 255 characters")
				.withValidator(instrumentsFacadeService::isUsernameAvailable, "Username already exists")
				.bind(UpdateUserRequest::getUsername, UpdateUserRequest::setUsername);

		binder.forField(emailField)
				.asRequired("Please fill this field")
				.withValidator(s -> s.length() >= 4, "Name must contain at least 4 characters")
				.withValidator(s -> s.length() <= 128, "Name must not exceed 128 characters")
				.withValidator(new EmailValidator("Please enter a valid email address"))
				.bind(UpdateUserRequest::getEmail, UpdateUserRequest::setEmail);
	}

	private void initListeners() {
		saveBtn.addClickListener(l -> {
			if (binder.validate().isOk()) {
				instrumentsFacadeService.updateUser(binder.getBean());
				showSuccessfulNotification("Profile updated successfully!");
			} else {
				log.error("Submitted Update Profile form with validation errors");
				showErrorNotification("An error occurred while submitting form");
			}
		});
	}

	private UpdateUserRequest getDefaultRequest() {
		return new UpdateUserRequest(instrumentsFacadeService.getAuthenticatedUser());
	}
}
