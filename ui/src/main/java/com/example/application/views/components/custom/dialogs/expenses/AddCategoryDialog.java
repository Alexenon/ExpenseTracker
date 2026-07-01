package com.example.application.views.components.custom.dialogs.expenses;

import com.example.application.data.requests.expenses.category.CreateCategoryRequest;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class AddCategoryDialog extends Dialog implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Category Name");
	private final Select<MonoIcon> iconSelector = new Select<>();
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());

	private final Binder<CreateCategoryRequest> binder = new Binder<>(CreateCategoryRequest.class);

	public AddCategoryDialog(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		setHeaderTitle("Add new category");
		initializeFields();
		initializeBinder();
	}

	private void initializeFields() {
		iconSelector.setLabel("Icon");

		iconSelector.setItems(instrumentsFacadeService.getCategoryIcons());
		iconSelector.setRenderer(createIconRenderer());

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				instrumentsFacadeService.createCategory(getRequest());
				showSuccessfulNotification("Category saved successfully!");
				this.close();
			} else {
				showErrorNotification("An error occurred while submitting form");
			}

		});

		add(nameField, iconSelector);
		this.getFooter().add(cancelButton, saveButton);
	}

	private void initializeBinder() {
		binder.forField(nameField)
				.asRequired("Please fill this field")
				.withValidator(new StringLengthValidator("Name length must be between 3 and 20 characters",
						3, 20));

		binder.forField(iconSelector)
				.asRequired("Please fill this field");
	}

	private static ComponentRenderer<Div, MonoIcon> createIconRenderer() {
		return new ComponentRenderer<>(icon -> {
			icon.addClassName("icon-selector-icon");
			Div div = new Div();
			div.addClassName("icon-selector-content");
			div.add(icon);
			return div;
		});
	}

	private CreateCategoryRequest getRequest() {
		return CreateCategoryRequest.builder()
				.name(nameField.getValue())
				.iconName(iconSelector.getValue().getRawIcon().name())
				.userId(instrumentsFacadeService.getAuthenticatedUser().getId())
				.build();
	}

}
