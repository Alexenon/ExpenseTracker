package com.example.application.views.components.custom.dialogs.expenses;

import com.example.application.InstrumentsFacadeService;
import com.example.application.tag.TagDTO;
import com.example.application.tag.UpdateTagRequest;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class EditTagDialog extends Dialog implements HasNotifications {

	private final TagDTO tagDTO;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Tag Name");
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());

	private final Binder<UpdateTagRequest> binder = new Binder<>(UpdateTagRequest.class);

	public EditTagDialog(TagDTO tagDTO, InstrumentsFacadeService instrumentsFacadeService) {
		this.tagDTO = tagDTO;
		this.instrumentsFacadeService = instrumentsFacadeService;
		setHeaderTitle("Edit '%s' tag".formatted(tagDTO.getName()));
		initializeFields();
		initializeBinder();
	}

	private void initializeFields() {
		nameField.setValue(tagDTO.getName());

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				instrumentsFacadeService.updateTag(getRequest());
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


	private UpdateTagRequest getRequest() {
		return new UpdateTagRequest(tagDTO.getId(), nameField.getValue());
	}

}
