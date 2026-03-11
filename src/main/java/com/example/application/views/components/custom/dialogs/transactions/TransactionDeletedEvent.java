package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.entities.crypto.Transaction;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dialog.Dialog;

public class TransactionDeletedEvent extends ComponentEvent<Dialog> {
    private final Transaction transaction;

    public TransactionDeletedEvent(Dialog source, Transaction transaction) {
        super(source, false);
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}