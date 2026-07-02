package com.example.application.views.components.custom.dialogs.expenses;

import com.example.application.InstrumentsFacadeService;
import com.example.application.category.CategoryDTO;
import com.example.application.category.UpdateCategoryRequest;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
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

public class EditCategoryDialog extends Dialog implements HasNotifications {

	private final CategoryDTO categoryDTO;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Category Name");
	private final Select<MonoIcon> iconSelector = new Select<>();
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());

	private final Binder<UpdateCategoryRequest> binder = new Binder<>(UpdateCategoryRequest.class);

	public EditCategoryDialog(CategoryDTO categoryDTO, InstrumentsFacadeService instrumentsFacadeService) {
		this.categoryDTO = categoryDTO;
		this.instrumentsFacadeService = instrumentsFacadeService;
		setHeaderTitle("Edit '%s' category".formatted(categoryDTO.getName()));
		initializeFields();
		initializeFieldValues();
		initializeBinder();
	}

	private void initializeFieldValues() {
		nameField.setValue(categoryDTO.getName());
		iconSelector.setItems(instrumentsFacadeService.getCategoryIcons());
		PictogramIcon.findByName(categoryDTO.getIconName())
				.ifPresent(icon -> iconSelector.setValue(icon.create()));
	}

	private void initializeFields() {
		iconSelector.setLabel("Icon");
		iconSelector.setRenderer(createIconRenderer());

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				instrumentsFacadeService.updateCategory(getRequest());
				showSuccessfulNotification("Category updated successfully!");
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

	private UpdateCategoryRequest getRequest() {
		Long id = categoryDTO.getId();
		String name = nameField.getValue();
		String iconName = iconSelector.getValue().getRawIcon().name();
		return new UpdateCategoryRequest(id, name, iconName);
	}

}
