package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.forms.layouts.TransactionalLayout;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.util.ArrayList;
import java.util.List;


/*
    | Type  | Symbol | Price | Amount tokens / currency | Total |
    | Buy   | SOL    | $110  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Buy   | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
* */

public class ProfitEmulatorTab extends BaseCalculatorTab {

    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final List<TransactionalLayout> transactionalLayouts = new ArrayList<>();
    private final Div assetDetailsContainer = new Div();

    public ProfitEmulatorTab(InstrumentsFacadeService instrumentsFacadeService,
                             PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Profit Buy/Sell Emulator", instrumentsFacadeService);
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
        buildTab();
    }

    private void buildTab() {
        assetSymbolField.addValueChangeListener(field -> {
            if (field.getHasValue().isEmpty()) {
                return;
            }

            Asset selectedAsset = field.getValue();
            transactionalLayouts.forEach(layout -> layout.setDefaultValues(selectedAsset));
        });
    }

    @Override
    protected Div createInputFieldsContainer() {
        Button addNewLayoutBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addNewLayoutBtn.setIconAfterText(false);
        addNewLayoutBtn.addClickListener(e -> createNewLayout());
        addNewLayoutBtn.addClassName("add-entity-btn");

        TransactionalLayout defaultLayout = new TransactionalLayout(instrumentsFacadeService, portfolioPerformanceTracker);
        defaultLayout.addClassName("buy-sell-layout");
        transactionalLayouts.add(defaultLayout);

        return new Div(assetSymbolField, addNewLayoutBtn, defaultLayout);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        return new Button("Don't click");
    }

    private void updateAssetDetailsContainer(Asset asset) {
        if (asset == null) {
            assetDetailsContainer.setVisible(false);
            return;
        }

        assetDetailsContainer.setVisible(true);

    }

    private Div assetDetailsLayout(Asset asset) {
        Div div = new Div();


        return div;
    }

    private Div statsItem(String labelText, double value) {
        return new Div(new Paragraph(labelText), new Paragraph(String.valueOf(value)));
    }

    private void createNewLayout() {
        TransactionalLayout newLayout = new TransactionalLayout(instrumentsFacadeService, portfolioPerformanceTracker);
        newLayout.addClassName("buy-sell-layout");

        MonoIcon deleteBtn = PictogramIcon.TRASH_CAN_OUTLINE.create();
        deleteBtn.addClickListener(e -> newLayout.removeFromParent());

        newLayout.add(deleteBtn);
        transactionalLayouts.add(newLayout);
        inputFieldsContainer.add(newLayout);
    }
}



