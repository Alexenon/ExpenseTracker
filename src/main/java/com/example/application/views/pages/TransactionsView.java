package com.example.application.views.pages;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Transactions")
@Route(value = "transactions", layout = MainLayout.class)
public class TransactionsView extends Main {

    private final InstrumentsFacadeService instrumentsFacadeService;

    private final TransactionsGrid transactionsGrid;

    @Autowired
    public TransactionsView(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.transactionsGrid = new TransactionsGrid(instrumentsFacadeService);
        buildPage();
    }

    private void buildPage() {
        transactionsGrid.setItems(instrumentsFacadeService.getAllTransactions());
        add(transactionsGrid);
    }

}
