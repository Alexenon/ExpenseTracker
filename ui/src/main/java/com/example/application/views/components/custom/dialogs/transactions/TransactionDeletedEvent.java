package com.example.application.views.components.custom.dialogs.transactions;

import com.example.application.transaction.TransactionDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dialog.Dialog;

public class TransactionDeletedEvent extends ComponentEvent<Dialog> {
    private final TransactionDTO transaction;

    public TransactionDeletedEvent(Dialog source, TransactionDTO transaction) {
        super(source, false);
        this.transaction = transaction;
    }

    public TransactionDTO getTransaction() {
        return transaction;
    }
}