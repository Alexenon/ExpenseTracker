package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.forms.layouts.BuySellForm;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.select.Select;
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

    private final PortfolioPerformanceTracker portfolioPerformanceTracker;

    private final AssetComboBox assetSymbolField;
    private final List<BuySellForm> buySellForms = new ArrayList<>();

    public ProfitEmulatorTab(InstrumentsFacadeService instrumentsFacadeService,
                             PortfolioPerformanceTracker portfolioPerformanceTracker) {
        super("Profit Buy/Sell Emulator", instrumentsFacadeService);
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
            buySellForms.forEach(form -> {
                double buyPrice = portfolioPerformanceTracker.getAverageBuyPrice(selectedAsset);
                double amountOfTokens = instrumentsFacadeService.getAmountOfTokens(selectedAsset);
                double totalInvested = buyPrice * amountOfTokens;
                form.getBuyPriceField().setValue(buyPrice);
                form.getAmountField().setValue(amountOfTokens);
                form.getTotalPriceField().setValue(totalInvested);
                form.getSellPriceField().setValue(instrumentsFacadeService.getAssetPrice(selectedAsset));
            });
        });
    }

    @Override
    protected Div createInputFieldsContainer() {
        Button addNewLayoutBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addNewLayoutBtn.setIconAfterText(false);
        addNewLayoutBtn.addClickListener(e -> createNewLayout());
        addNewLayoutBtn.addClassName("add-entity-btn");

        BuySellForm defaultForm = new BuySellForm();
        defaultForm.addClassName("buy-sell-layout");
        buySellForms.add(defaultForm);

        return new Div(assetSymbolField, addNewLayoutBtn, defaultForm);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        return new Button();
    }

    private void createNewLayout() {
        BuySellForm newFormLayout = new BuySellForm();
        newFormLayout.addClassName("buy-sell-layout");

        Select<String> typeField = new Select<>();
        typeField.setLabel("TRANSACTION TYPE");
        typeField.setItems(List.of("BUY", "SELL"));
        typeField.setValue("BUY");
        newFormLayout.addComponentAsFirst(typeField);
        newFormLayout.addComponentAtIndex(5, typeField);

        // TODO: Add other listeners for amountField, total...

        MonoIcon deleteBtn = PictogramIcon.TRASH_CAN_OUTLINE.create();
        deleteBtn.addClickListener(e -> newFormLayout.removeFromParent());

        newFormLayout.add(deleteBtn);
        buySellForms.add(newFormLayout);
        inputFieldsContainer.add(newFormLayout);
    }
}



