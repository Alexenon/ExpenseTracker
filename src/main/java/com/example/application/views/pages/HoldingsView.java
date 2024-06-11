package com.example.application.views.pages;

import com.example.application.data.models.Currency;
import com.example.application.data.models.Holding;
import com.example.application.data.models.Range;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;

@AnonymousAllowed
@PageTitle("Crypto Holdings")
@Route(value = "holdings", layout = MainLayout.class)
public class HoldingsView extends Main {

    private final Grid<Holding> grid = new Grid<>(Holding.class);
    List<Holding> holdings = new ArrayList<>();

    public HoldingsView() {
        getStyle().set("margin-top", "100px");
        fill();

        grid.addColumn("currency.name");

        Grid.Column<Holding> preferredRangeCol = grid.addColumn(h -> {
            Range preferred = h.getPreferredRange();
            return preferred.getTo() + " <> " + preferred.getTo();
        }).setHeader("Preferred Range");

        Grid.Column<Holding> wantedRangeCol = grid.addColumn(h -> {
            Range wantedRange = h.getWantedRange();
            return wantedRange.getTo() + " <> " + wantedRange.getTo();
        }).setHeader("Wanted Range");

        // Create a header row
        HeaderRow topRow = grid.prependHeaderRow();

        // group two columns under the same label
        topRow.join(preferredRangeCol, wantedRangeCol)
                .setComponent(new Label("Ranges`"));

        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        add(grid);
    }

    private void fill() {
        Holding h1 = new Holding(
                new Currency("BTC", 20_000, 64_000, 70_000),
                new Range(64_000, 52_000),
                new Range(50_000, 20_000),
                0
        );

        Holding h2 = new Holding(
                new Currency("ETH", 3600, 2000, 4600),
                new Range(3500, 2500),
                new Range(3000, 2000),
                0
        );

        holdings.add(h1);
        holdings.add(h2);
    }

}
