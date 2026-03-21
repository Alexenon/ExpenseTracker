package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CompactFormatter;
import com.example.application.utils.common.formatters.number.CurrencyFormatter;
import com.example.application.utils.common.formatters.number.PercentageFormatter;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class BaseCalculatorTab extends Tab implements BeforeEnterObserver {

    protected final InstrumentsFacadeService instrumentsFacadeService;

    protected AmountFormatter amountFormatter = AmountFormatter.withDefaults();
    protected CompactFormatter compactFormatter = CompactFormatter.withDefaults();
    protected CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();
    protected PercentageFormatter percentageFormatter = PercentageFormatter.withDefaults();

    protected Div inputFieldsContainer;
    protected Button displayResultsBtn;
    protected Div resultsContainer = new Container("result-items");

    public BaseCalculatorTab(String label, InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;

        addClassName("compare-tab-content");

        addAttachListener(e -> {
            this.inputFieldsContainer = createInputFieldsContainer();
            this.displayResultsBtn = createDisplayResultsBtn();
            add(
                    new H3(label),
                    inputFieldsContainer,
                    displayResultsBtn,
                    new Container("results", new H3("Results"), resultsContainer)
            );

            inputFieldsContainer.addClassName("comparation-body");
        });
    }

    protected abstract Div createInputFieldsContainer();

    protected abstract Button createDisplayResultsBtn();

    protected Div createResultItem(String labelText, BigDecimal value) {
        return createResultItem(labelText, String.format("$%.2f", value.setScale(2, RoundingMode.HALF_UP)));
    }

    protected Div createResultItem(String labelText, BigDecimal value, String tooltipText) {
        return createResultItem(labelText, String.format("$%.2f", value.setScale(2, RoundingMode.HALF_UP)), tooltipText);
    }

    protected Div createResultItem(String labelText, String valueText) {
        return createResultItem(labelText, new Span(valueText), null);
    }

    protected Div createResultItem(String labelText, String valueText, String tooltipText) {
        return createResultItem(labelText, new Span(valueText), tooltipText);
    }

    protected Div createResultItem(String labelText, Component content) {
        Div itemContainer = new Div();
        itemContainer.addClassName("result-item");
        itemContainer.add(new Paragraph(labelText), content);
        return itemContainer;
    }

    protected Div createResultItem(String labelText, Component content, String tooltipText) {
        Div itemContainer = new Div();
        Paragraph label = new Paragraph(labelText);
        itemContainer.addClassName("result-item");
        itemContainer.add(label, content);

        if (tooltipText != null && !tooltipText.isEmpty()) {
            MonoIcon infoIcon = PictogramIcon.INFORMATION_OUTLINE.create();
            infoIcon.addClassName("tooltip-info-icon");
            Tooltip tooltip = Tooltip.forComponent(infoIcon);
            tooltip.setPosition(Tooltip.TooltipPosition.TOP);
            tooltip.setText(tooltipText);
            label.add(infoIcon);
        }

        return itemContainer;
    }

}
