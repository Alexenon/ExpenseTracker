package com.example.application.views.pages;

import com.example.application.data.models.Currency;
import com.example.application.data.models.Holding;
import com.example.application.views.components.complex_components.dialogs.AddHoldingDialog;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@AnonymousAllowed
@PageTitle("Crypto Holdings")
@Route(value = "holdings", layout = MainLayout.class)
public class HoldingsView extends Main {

    private final Grid<Holding> grid = new Grid<>(Holding.class);

    public HoldingsView() {
        getStyle().set("margin-top", "100px");

        Button addBtn = new Button("Add");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(event -> {
            AddHoldingDialog dialog = new AddHoldingDialog();
            dialog.open();
            dialog.addClickSaveBtnListener(grid -> updateGrid());
        });

        grid.removeAllColumns();
//        grid.addColumns("name");

//        grid.addColumn(h -> {
//            Range preferred = h.getPreferredRange();
//            return preferred.getTo() + " <> " + preferred.getFrom();
//        }).setHeader("Preferred Range");
//
//        grid.addColumn(h -> {
//            Range wantedRange = h.getWantedRange();
//            return wantedRange.getTo() + " <> " + wantedRange.getFrom();
//        }).setHeader("Wanted Range");

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        updateGrid();
        add(addBtn, grid);
    }

    private void updateGrid() {
//        grid.setItems(null);
    }

    private List<Currency> getCurrencyList() {
        return List.of(
                new Currency("BTC", 64_000, 32_000, 73_000),
                new Currency("ETH", 3600, 2000, 4600)
        );
    }

    private List<Holding> getHoldings() {
        return List.of(
                new Holding("BTC", List.of(60_000.0, 50_000.0, 40_000.0), ""),
                new Holding("ETH", List.of(3500.0, 3000.0), "")
        );
    }

}
