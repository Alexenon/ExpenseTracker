package com.example.application.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.theme.lumo.LumoIcon;

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

    // TODO:
    //  - https://www.w3schools.com/charsets/tryit.asp?deci=8776&ent=asymp
    //  - Amount Tokens in USDT
    //  - Amount USDT in Tokens
    private void initialize() {
        addNewPriceLayoutBtn.addClickListener(e -> priceLayoutContainer.add(new PriceLayout()));
        priceLayoutContainer.add(new PriceLayout());
        add(addNewPriceLayoutBtn, priceLayoutContainer);
    }

    /*
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private static class PriceLayout extends Div {

        private final RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        private final NumberField price = new NumberField("Price");
        private final NumberField tokenAmount = new NumberField("Amount in Tokens");
        private final NumberField usdtAmount = new NumberField("Amount in USDT");
        private final NumberField percent = new NumberField("Percentage amount");
        private final Button removeIcon = new Button(LumoIcon.CROSS.create());
        private final Checkbox markAsBought = new Checkbox("Mark as bought");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());
        private boolean isEditMode;

        public PriceLayout() {
            init();
            add(price, tokenAmount, usdtAmount, percent, markAsBought, removeIcon);
        }

        private void init() {
//            removeIcon.setColor("red");
            removeIcon.addClickListener(event -> this.removeFromParent());
            tokenAmount.setSuffixComponent(new Paragraph("BTC"));
            price.setPrefixComponent(new Paragraph("$"));
            usdtAmount.setPrefixComponent(new Paragraph("$"));
            percent.setSuffixComponent(new Paragraph("%"));
            tokenAmount.setMin(0);
            price.setMin(0);
            percent.setMin(0);
            percent.setMax(100);
            setup();
        }

        private void setup() {
            radioButtonGroup.setLabel("Additional providers");
            radioButtonGroup.setItems("None", "Token Amount", "USDT Amount", "Percentage");
            radioButtonGroup.setValue("None");

            radioButtonGroup.addValidationStatusChangeListener(e -> {
                String selected = radioButtonGroup.getValue();
                tokenAmount.setVisible(selected.equals("Token Amount"));
                usdtAmount.setVisible(selected.equals("USDT Amount"));
                percent.setVisible(selected.equals("Percentage"));
            });

            editBtn.addClickListener(e -> {
                isEditMode = !isEditMode;
                radioButtonGroup.setVisible(isEditMode);
                editBtn.setIcon(isEditMode ? LumoIcon.CHECKMARK.create() : LumoIcon.EDIT.create());

                price.setReadOnly(!isEditMode);
                tokenAmount.setReadOnly(!isEditMode);
                usdtAmount.setReadOnly(!isEditMode);
                percent.setReadOnly(!isEditMode);
                markAsBought.setVisible(isEditMode);
                removeIcon.setVisible(isEditMode);
            });
        }

        private Component fieldWrapper(Component field) {
            HorizontalLayout layout = new HorizontalLayout(field);
            layout.add(new Span());
            return layout;
        }


    }

}
