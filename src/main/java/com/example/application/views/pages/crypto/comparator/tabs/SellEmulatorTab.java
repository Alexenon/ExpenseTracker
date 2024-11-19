package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.complex_components.fields.BuySellForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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

public class SellEmulatorTab extends BaseCalculatorTab {

    private final List<BuySellForm> formList = new ArrayList<>();

    public SellEmulatorTab(InstrumentsFacadeService instrumentsFacadeService) {
        super("Profit Buy/Sell Emulator", instrumentsFacadeService);
    }

    @Override
    protected Div createInputFieldsContainer() {
        Button addNewLayoutBtn = new Button(LumoIcon.PLUS.create());
        addNewLayoutBtn.addClickListener(e -> {
            createNewLayout();
        });

        BuySellForm defaultForm = new BuySellForm();

        return new Div(addNewLayoutBtn, defaultForm);
    }

    @Override
    protected Button createDisplayResultsBtn() {
        return new Button();
    }

    private void createNewLayout() {
        BuySellForm newForm = new BuySellForm();
        formList.add(newForm);
        inputFieldsContainer.add(newForm);
    }
}



