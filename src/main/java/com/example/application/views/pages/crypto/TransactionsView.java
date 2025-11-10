package com.example.application.views.pages.crypto;

import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.custom.dialogs.DialogFactory;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@PermitAll
@PageTitle("Transactions")
@Route(value = "transactions", layout = MainLayout.class)
public class TransactionsView extends DefaultPage {

    private final Portfolio portfolio;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final DialogFactory dialogFactory;

    private final TransactionsGrid transactionsGrid;

    @Autowired
    public TransactionsView(Portfolio portfolio,
							InstrumentsFacadeService instrumentsFacadeService,
							DialogFactory dialogFactory) {
        this.portfolio = Objects.requireNonNull(portfolio, "portfolio");
        this.instrumentsFacadeService = Objects.requireNonNull(instrumentsFacadeService, "instrumentsFacadeService");
		this.dialogFactory = Objects.requireNonNull(dialogFactory, "dialogFactory");
        this.transactionsGrid = new TransactionsGrid(instrumentsFacadeService, dialogFactory);
        buildPage();
    }

    private void buildPage() {
        transactionsGrid.setItems(instrumentsFacadeService.getTransactions(portfolio));
        add(transactionsGrid);
    }

}
