package com.example.application.views.components.custom.dialogs;

import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.dialogs.transactions.TransactionDetailsDialog;
import com.example.application.views.components.custom.dialogs.transactions.transfer.TransferTransactionDialog;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@SpringComponent
@UIScope
public class DialogFactory {

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final ApplicationEventPublisher publisher;

	@Autowired
	public DialogFactory(InstrumentsFacadeService instrumentsFacadeService,
						 ApplicationEventPublisher publisher) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.publisher = publisher;
	}

	public TransferTransactionDialog transferTransactionDialog(Transaction transaction) {
		return new TransferTransactionDialog(transaction, instrumentsFacadeService, publisher);
	}

	public TransactionDetailsDialog transactionDetailsDialog(Transaction transaction) {
		return new TransactionDetailsDialog(transaction, instrumentsFacadeService, this);
	}

}
