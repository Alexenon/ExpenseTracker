package com.example.application.views.pages.settings.tabs;

import com.example.application.InstrumentsFacadeService;
import com.example.application.user.domain.UpdateUserPasswordRequest;
import com.example.application.views.components.utils.CommonButtons;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class PasswordTab extends AbstractSettingsTab implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final PasswordField currentPasswordField = new PasswordField("Current Password");
	private final PasswordField newPasswordField = new PasswordField("New Password");
	private final PasswordField confirmPasswordField = new PasswordField("Confirm Password");
	private final Button saveBtn = CommonButtons.createSaveButton("Save");

	private final Binder<UpdateUserPasswordRequest> binder = new Binder<>(UpdateUserPasswordRequest.class);

	public PasswordTab(InstrumentsFacadeService instrumentsFacadeService) {
		super("Password", VaadinIcon.PASSWORD.create());
		this.instrumentsFacadeService = instrumentsFacadeService;
	}

	private void initBinder() {
		binder.forField(currentPasswordField)
				.asRequired("Please fill this field")
				.withValidator(instrumentsFacadeService::isPasswordCorrect, "Current password is incorrect. Please try again.");

		binder.forField(newPasswordField)
				.asRequired("Please fill this field")
				.withValidator(new StringLengthValidator("Password must be between 4 and 128 characters", 4, 128));

		binder.forField(confirmPasswordField)
				.asRequired("Please fill this field")
				.withValidator(s -> s.equals(newPasswordField.getValue()), "Both password should be the same");
	}

	@Override
	public Div getContent() {
		return new Div(currentPasswordField, newPasswordField, confirmPasswordField, saveBtn);
	}

	private void init() {
		saveBtn.addClickListener(e -> {
			if (binder.validate().isOk()) {
				boolean isPasswordUpdated = instrumentsFacadeService.changeUserPassword(getRequest());
				if (isPasswordUpdated) {
					clearFieldValues();
					showSuccessfulNotification("The password was successfully updated!");
				} else {
					showWarningNotification("New password cannot be the same as your current password.");
				}
			} else {
				showErrorNotification("An error occurred while submitting form");
			}
		});
	}

	private void clearFieldValues() {
		currentPasswordField.clear();
		newPasswordField.clear();
		confirmPasswordField.clear();
	}

	private UpdateUserPasswordRequest getRequest() {
		return new UpdateUserPasswordRequest(
				instrumentsFacadeService.getAuthenticatedUser().getId(),
				confirmPasswordField.getValue()
		);
	}

}
