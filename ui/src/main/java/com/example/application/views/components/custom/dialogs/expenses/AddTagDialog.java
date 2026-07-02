package com.example.application.views.components.custom.dialogs.expenses;

import com.example.application.InstrumentsFacadeService;
import com.example.application.tag.CreateTagRequest;
import com.example.application.tag.UpdateTagRequest;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class AddTagDialog extends Dialog implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Tag Name");
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());

	private final Binder<UpdateTagRequest> binder = new Binder<>(UpdateTagRequest.class);

	public AddTagDialog(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		setHeaderTitle("Add new tag");
		initializeFields();
		initializeBinder();
	}

	private void initializeFields() {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				instrumentsFacadeService.createTag(getRequest());
				showSuccessfulNotification("Tag updated successfully!");
				this.close();
			} else {
				showErrorNotification("An error occurred while submitting form");
			}

		});

		add(nameField);
		this.getFooter().add(cancelButton, saveButton);
	}

	private void initializeBinder() {
		binder.forField(nameField)
				.asRequired("Please fill this field")
				.withValidator(new StringLengthValidator("Name length must be between 3 and 20 characters",
						3, 20))
				.withValidator(instrumentsFacadeService::isTagNameAvailable, "There is already a tag with this name");
	}


	private CreateTagRequest getRequest() {
		return new CreateTagRequest(nameField.getValue(), instrumentsFacadeService.getAuthenticatedUser().getId());
	}

}
