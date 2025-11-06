package com.example.application.views.components.portfolio;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.Optional;
import java.util.function.Consumer;

public class AddPortfolioDialog extends Dialog implements HasNotifications {

	private static final int MIN_NAME_LENGTH = 4;
	private static final int MAX_NAME_LENGTH = 10;

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final TextField nameField = new TextField("Portfolio Name");
	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());
	private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

	private Portfolio portfolio;
	private final Binder<Portfolio> binder = new Binder<>(Portfolio.class);

	public AddPortfolioDialog(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		build();
	}

	private void build() {
		setHeaderTitle("Add New Portfolio");
		add(nameField);
		initializeButtons();
		initializeValidation();
	}

	private void initializeButtons() {
		closeBtn.addClickShortcut(Key.ESCAPE);
		closeBtn.addClassName("modal-close-btn");
		getHeader().add(closeBtn);

		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				String name = nameField.getValue();
				portfolio = instrumentsFacadeService.createPortfolio(name);
				showSuccessfulNotification("Porfolio '%s' successfully created".formatted(name));
				close();
			} else {
				showErrorNotification("Something went wrong");
			}
		});
		getFooter().add(saveButton, cancelButton);
	}

	private void initializeValidation() {
		nameField.setRequiredIndicatorVisible(true);
		nameField.setAutocomplete(Autocomplete.OFF);
		nameField.setMinLength(MIN_NAME_LENGTH);
		nameField.setMaxLength(MAX_NAME_LENGTH);
		nameField.setI18n(new TextField.TextFieldI18n()
				.setRequiredErrorMessage("Porfolio Name cannot be blank")
				.setMinLengthErrorMessage("Porfolio Name must be have at least %s characters".formatted(MIN_NAME_LENGTH))
				.setMaxLengthErrorMessage("Porfolio Name must be have maximum %s characters".formatted(MAX_NAME_LENGTH)));

		binder.setBean(new Portfolio());
		binder.forField(nameField)
				.asRequired("Porfolio Name cannot be blank")
				.withValidator(s -> s.length() >= MIN_NAME_LENGTH, "Porfolio Name must be have at least %s characters".formatted(MIN_NAME_LENGTH))
				.withValidator(s -> s.length() <= MAX_NAME_LENGTH, "Porfolio Name must be have maximum %s characters".formatted(MAX_NAME_LENGTH))
				.withValidator(s -> instrumentsFacadeService.getPortfolioByName(s).isEmpty(), "There is already a portfolio with such name")
				.bind(Portfolio::getName, Portfolio::setName);
	}

	public void addOnSuccessfullSaveListener(Consumer<?> listener) {
		saveButton.addClickListener(e -> {
			if (getPortfolio().isPresent())
				listener.accept(null);
		});
	}

	public Optional<Portfolio> getPortfolio() {
		return Optional.ofNullable(portfolio);
	}

}