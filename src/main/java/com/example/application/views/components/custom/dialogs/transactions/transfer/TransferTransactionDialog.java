package com.example.application.views.components.custom.dialogs.transactions.transfer;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

@UIScope
@SpringComponent
public class TransferTransactionDialog extends Dialog implements HasNotifications {

	private final Transaction transaction;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final  ApplicationEventPublisher eventPublisher;

	private final Select<Portfolio> select = new Select<>();
	private final Checkbox replaceCheckbox = new Checkbox("Replace");

	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel");
	private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

	@Autowired
	public TransferTransactionDialog(Transaction transaction,
									 InstrumentsFacadeService instrumentsFacadeService,
									 ApplicationEventPublisher eventPublisher) {
		this.transaction = Objects.requireNonNull(transaction, "transaction");
		this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");
		this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
		build();
	}

	private void build() {
		select.setLabel("Portfolio");
		select.setItems(getPortfoliosWithoutCurrentOne());
		select.setItemLabelGenerator(Portfolio::getName);
		replaceCheckbox.setTooltipText("Moves the transaction to the selected portfolio instead of copying it");

		closeBtn.addClickShortcut(Key.ESCAPE);
		closeBtn.addClassName("modal-close-btn");
		getHeader().add(closeBtn);

		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (isFilledCorrectly()) {
				Portfolio portfolio = select.getValue();
				boolean replace = replaceCheckbox.getValue();
				Transaction transferred = instrumentsFacadeService.transferTransaction(transaction, portfolio, replace);
				displaySuccessfullNotification(replace, portfolio);
				eventPublisher.publishEvent(new TransactionTransferedEvent(this, transferred));
				close();
			} else {
				showErrorNotification("Something went wrong");
			}
		});

		add(
				select,
				replaceCheckbox
		);
		getFooter().add(saveButton, cancelButton);
	}

	private boolean isFilledCorrectly() {
		return select.getValue() != null;
	}

	private void displaySuccessfullNotification(boolean replace, Portfolio portfolio) {
		String action = replace ? "transfered" : "copied";
		showSuccessfulNotification("Transaction successfully %s to '%s' portfolio".formatted(action, portfolio.getName()));
	}

	private List<Portfolio> getPortfoliosWithoutCurrentOne() {
		return instrumentsFacadeService.getUserPortfolios()
				.stream()
				.filter(p -> !transaction.getPortfolio().equals(p))
				.toList();
	}

}
