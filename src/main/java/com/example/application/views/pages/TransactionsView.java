package com.example.application.views.pages;

import com.example.application.data.models.InstrumentsProvider;
import com.example.application.services.crypto.InstrumentsService;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Yearly expenses")
@Route(value = "transactions", layout = MainLayout.class)
public class TransactionsView extends Main {

    private final InstrumentsService instrumentService;
    private final InstrumentsProvider instrumentsProvider;

    private final TransactionsGrid transactionsGrid;

    @Autowired
    public TransactionsView(InstrumentsService instrumentService, InstrumentsProvider instrumentsProvider) {
        this.instrumentService = instrumentService;
        this.instrumentsProvider = instrumentsProvider;
        this.transactionsGrid = new TransactionsGrid(instrumentService, instrumentsProvider);
        buildPage();
    }

    private void buildPage() {
        transactionsGrid.setItems(instrumentService.getTransactions());
        add(transactionsGrid);
    }

}
