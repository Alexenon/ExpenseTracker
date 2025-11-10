package com.example.application.views.components.custom.dialogs.transactions.transfer;

import com.example.application.entities.crypto.Transaction;
import org.springframework.context.ApplicationEvent;

public class TransactionTransferedEvent extends ApplicationEvent {

	private final Transaction transaction;

	public TransactionTransferedEvent(Object source, Transaction transaction) {
		super(source);
		this.transaction = transaction;
	}

	public Transaction getTransaction() {
		return transaction;
	}

}