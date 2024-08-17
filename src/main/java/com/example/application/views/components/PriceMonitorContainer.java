package com.example.application.views.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * <p>Container to track a currency wanted price when to buy/sell</p>
 * <br>
 *
 * <h3>Features:</h3>
 * <ul>
 *  <li>Wanted price</li>
 * Optional:
 *  <li>Amount of Tokens</li>
 *  <li>Amount in USDT</li>
 *  <li>Amount in % of USDT</li>
 * </ul>
 */
public class PriceMonitorContainer extends Div {

    private final Button addNewPriceLayoutBtn = new Button(LumoIcon.PLUS.create());
    private final Div priceLayoutContainer = new Div();

    public PriceMonitorContainer() {
        initialize();
    }

    private void initialize() {
        addNewPriceLayoutBtn.addClickListener(e -> priceLayoutContainer.add(new PriceLayout()));
        priceLayoutContainer.add(new PriceLayout());
        add(addNewPriceLayoutBtn, priceLayoutContainer);
    }

    /*
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private static class PriceLayout extends Div {

        private static final int INDEX_OF_AMOUNT_FIELD = 2;

        private final RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();

        private final NumberField price = new NumberField("Price");
        private final NumberField tokenAmount = new NumberField("Amount in Tokens");
        private final NumberField usdtAmount = new NumberField("Amount in USDT");
        private final NumberField percentAmount = new NumberField("Percentage amount");
        private final Span amountHelperSpan = new Span("&asymp;");

        private final Button removeIcon = new Button(LumoIcon.CROSS.create());
        private final Checkbox markAsBought = new Checkbox("Mark as bought");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());
        private boolean isEditMode;

        public PriceLayout() {
            init();
            Div amountHelper = new Div(amountHelperSpan);
            add(radioButtonGroup, price, tokenAmount, amountHelper, markAsBought, removeIcon);
        }

        private void init() {
            removeIcon.addClickListener(event -> this.removeFromParent());
            tokenAmount.setSuffixComponent(new Paragraph("BTC"));
            price.setPrefixComponent(new Paragraph("$"));
            usdtAmount.setPrefixComponent(new Paragraph("$"));
            percentAmount.setSuffixComponent(new Paragraph("%"));
            tokenAmount.setMin(0);
            price.setMin(0);
            percentAmount.setMin(0);
            percentAmount.setMax(100);
            setup();
        }

        private void setup() {
            radioButtonGroup.setLabel("Additional providers");
            radioButtonGroup.setItems("None", "Token Amount", "USDT Amount", "Percentage");
            radioButtonGroup.setValue("None");
            radioButtonGroup.addValueChangeListener(e -> onRadioButtonChange());

            price.setValue(0.0);
            price.setValueChangeMode(ValueChangeMode.EAGER);

            tokenAmount.setValue(0.0);
            tokenAmount.setValueChangeMode(ValueChangeMode.EAGER);
            tokenAmount.addValueChangeListener(l -> amountHelperSpan.setText(getTokenAmountListenerValue()));

            usdtAmount.setValue(0.0);
            usdtAmount.setValueChangeMode(ValueChangeMode.EAGER);
            usdtAmount.addValueChangeListener(l -> amountHelperSpan.setText(getUSDTAmountListenerValue()));

            editBtn.addClickListener(e -> toggleEditMode());
        }

        private void onRadioButtonChange() {
            switch (radioButtonGroup.getValue()) {
                case "Token Amount" -> handleTokenAmountSelection();
                case "USDT Amount" -> handleUsdtAmountSelection();
                case "Percentage" -> handlePercentageSelection();
                default -> getComponentAt(INDEX_OF_AMOUNT_FIELD).setVisible(false);
            }
        }

        private void handleTokenAmountSelection() {
            amountHelperSpan.setText(getTokenAmountListenerValue());
            price.addValueChangeListener(l -> amountHelperSpan.setText(getTokenAmountListenerValue()));
            replaceAmountField(tokenAmount);
        }

        private void handleUsdtAmountSelection() {
            amountHelperSpan.setText(getUSDTAmountListenerValue());
            price.addValueChangeListener(l -> amountHelperSpan.setText(getUSDTAmountListenerValue()));
            replaceAmountField(usdtAmount);
        }

        private void handlePercentageSelection() {
            replaceAmountField(percentAmount);
        }

        private void toggleEditMode() {
            isEditMode = !isEditMode;
            radioButtonGroup.setVisible(isEditMode);
            editBtn.setIcon(isEditMode ? LumoIcon.CHECKMARK.create() : LumoIcon.EDIT.create());

            price.setReadOnly(!isEditMode);
            tokenAmount.setReadOnly(!isEditMode);
            usdtAmount.setReadOnly(!isEditMode);
            percentAmount.setReadOnly(!isEditMode);
            markAsBought.setVisible(isEditMode);
            removeIcon.setVisible(isEditMode);
        }

        private String getTokenAmountListenerValue() {
            double priceValue = Objects.requireNonNullElse(price.getValue(), 0.0);
            double tokenAmountValue = Objects.requireNonNullElse(tokenAmount.getValue(), 0.0);

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            currencyFormat.setMaximumFractionDigits(0);

            return  "~" + currencyFormat.format(priceValue * tokenAmountValue);
        }

        private String getUSDTAmountListenerValue() {
            double priceValue = Objects.requireNonNullElse(price.getValue(), 0.0);
            double usdtAmountValue = Objects.requireNonNullElse(usdtAmount.getValue(), 0.0);
            double amountTokens = usdtAmountValue / priceValue;

            return "~" + amountTokens + "BTC";
        }

        public void replaceAmountField(NumberField newComponent) {
            NumberField oldComponent = (NumberField) getComponentAt(INDEX_OF_AMOUNT_FIELD);
            newComponent.setValue(oldComponent.getValue());
            replace(oldComponent, newComponent);
            newComponent.setVisible(true);
        }

    }

}
