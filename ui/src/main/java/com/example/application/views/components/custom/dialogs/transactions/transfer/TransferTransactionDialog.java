package com.example.application.views.components.custom.dialogs.transactions.transfer;

import com.example.application.portfolio.PortfolioDTO;
import com.example.application.transaction.TransactionDTO;
import com.example.application.InstrumentsFacadeService;
import com.example.application.views.components.custom.dialogs.transactions.TransactionDeletedEvent;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

@Slf4j
@UIScope
@SpringComponent
public class TransferTransactionDialog extends Dialog implements HasNotifications {

	private final TransactionDTO transaction;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final Select<PortfolioDTO> select = new Select<>();
	private final Checkbox replaceCheckbox = new Checkbox("Replace");

	private final Button saveButton = new Button("Save");
	private final Button cancelButton = new Button("Cancel", e -> this.close());
	private final Button closeBtn = new Button(LumoIcon.CROSS.create(), e -> this.close());

	@Autowired
	public TransferTransactionDialog(TransactionDTO transaction,
									 InstrumentsFacadeService instrumentsFacadeService)
	{
		this.transaction = Objects.requireNonNull(transaction, "transaction");
		this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");
		build();
	}

	private void build() {
		setHeaderTitle("Transfer transaction");
		select.setLabel("Portfolio");
		select.setItems(getPortfoliosWithoutCurrentOne());
		select.setItemLabelGenerator(PortfolioDTO::getName);
		replaceCheckbox.setTooltipText("Moves the transaction to the selected portfolio instead of copying it");

		closeBtn.addClickShortcut(Key.ESCAPE);
		closeBtn.addClassName("modal-close-btn");
		getHeader().add(closeBtn);

		saveButton.addClickShortcut(Key.ENTER);
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		saveButton.addClickListener(e -> {
			if (!isFilledCorrectly()) {
				showErrorNotification("Error! Please fill all the fields");
				return;
			}

			PortfolioDTO portfolio = select.getValue();
			boolean isReplaced = replaceCheckbox.getValue();

			if (!isReplaced) {
				manageSuccesfullTransferAction(portfolio, false);
				return;
			}

			ConfirmDialog confirmationDialog = getConfirmationDialog(transaction);
			confirmationDialog.open();
			confirmationDialog.addConfirmListener(l -> {
				TransactionDTO replacedTransaction = manageSuccesfullTransferAction(portfolio, true);
				UI.getCurrent().access(() -> ComponentUtil.fireEvent(UI.getCurrent(), new TransactionDeletedEvent(this, replacedTransaction)));
			});
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

	private TransactionDTO manageSuccesfullTransferAction(PortfolioDTO portfolio, boolean isReplaced) {
		TransactionDTO transferred = instrumentsFacadeService.transferTransaction(transaction.getId(), portfolio.getId(), isReplaced);
		displaySuccessfullNotification(isReplaced, portfolio);
		this.close();
		return transferred;
	}

	private void displaySuccessfullNotification(boolean replace, PortfolioDTO portfolio) {
		String action = replace ? "transfered" : "copied";
		showSuccessfulNotification("Transaction successfully %s to '%s' portfolio".formatted(action, portfolio.getName()));
	}

	private List<PortfolioDTO> getPortfoliosWithoutCurrentOne() {
		return instrumentsFacadeService.getUserPortfolios()
				.stream()
				.filter(p -> !Objects.equals(p.getId(), transaction.getPortfolioId()))
				.toList();
	}

	private ConfirmDialog getConfirmationDialog(TransactionDTO replacedTransaction) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Replace this transaction");
		dialog.setText("Are you sure you want to permanently delete transaction from this portfolio, and move it to the '%s' portfolio?"
				.formatted(select.getValue().getName()));

		dialog.setConfirmText("Replace");
		dialog.setConfirmButtonTheme("error primary");
		dialog.setCancelable(true);

		return dialog;
	}

}