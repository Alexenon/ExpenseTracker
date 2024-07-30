package com.example.application.views.components;

import com.example.application.views.components.complex_components.inputs.AmountField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * <p>Container to track a currency wanted price when to buy/sell</p><br>
 * <p>
 * Features:
 * <li>Wanted price</li>
 * <li>Amount in USDT</li>
 * <li>Amount in % of USDT</li>
 */
public class PriceMonitorContainer extends Div {

    private final RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
    private final Button addNewPriceLayoutBtn = new Button(LumoIcon.PLUS.create());
    private final Div priceLayoutContainer = new Div();
    private Button editBtn = new Button(LumoIcon.EDIT.create());
    private boolean isEditMode;

    public PriceMonitorContainer() {
        init();
        hide();
        priceLayoutContainer.add(new PriceLayout());
        add(radioButtonGroup, addNewPriceLayoutBtn, editBtn, priceLayoutContainer);

        add(new AmountField());
    }

    private void init() {
//        radioButtonGroup.addThemeVariants(RadioGroupVariant.LUMO_HELPER_ABOVE_FIELD);
        radioButtonGroup.setLabel("Additional providers");
        radioButtonGroup.setItems("None", "USDT Amount", "Percentage");
        radioButtonGroup.setValue("None");

        radioButtonGroup.addValidationStatusChangeListener(e -> {
            String selected = radioButtonGroup.getValue();
            priceLayoutContainer.getChildren().forEach(layout -> {
                NumberField price = layout.getElement().getChild(1).as(NumberField.class);
                IntegerField percentage = layout.getElement().getChild(2).as(IntegerField.class);
                price.setVisible(selected.equals("USDT Amount"));
                percentage.setVisible(selected.equals("Percentage"));
            });
        });

        addNewPriceLayoutBtn.addClickListener(e -> priceLayoutContainer.add(new PriceLayout()));
        editBtn.addClickListener(e -> {
            isEditMode = !isEditMode;
            radioButtonGroup.setVisible(isEditMode);
            addNewPriceLayoutBtn.setVisible(isEditMode);
            Icon icon = isEditMode ? LumoIcon.CHECKMARK.create() : LumoIcon.EDIT.create();
            editBtn.setIcon(icon);

            priceLayoutContainer.getChildren().forEach(layout -> {
                NumberField amount = layout.getElement().getChild(0).as(NumberField.class);
                NumberField price = layout.getElement().getChild(1).as(NumberField.class);
                IntegerField percentage = layout.getElement().getChild(2).as(IntegerField.class);
                Checkbox markAsBought = layout.getElement().getChild(3).as(Checkbox.class);
                Button removeIcon = layout.getElement().getChild(4).as(Button.class);

                amount.setReadOnly(!isEditMode);
                price.setReadOnly(!isEditMode);
                percentage.setReadOnly(!isEditMode);
                markAsBought.setVisible(isEditMode);
                removeIcon.setVisible(isEditMode);
            });
        });
    }

    private void hide() {
        radioButtonGroup.setVisible(false);
        addNewPriceLayoutBtn.setVisible(false);
    }

    private static class PriceLayout extends Div {
        private final NumberField price = new NumberField("Token Price");
        private final NumberField usdtAmount = new NumberField("USDT Amount");
        private final IntegerField percent = new IntegerField("Percent");
        private final Button removeIcon = new Button(LumoIcon.CROSS.create());
        private final Checkbox markAsBought = new Checkbox("Mark as bought");

        public PriceLayout() {
            init();
            add(price, usdtAmount, percent, markAsBought, removeIcon);
        }

        private void init() {
//            removeIcon.setColor("red");
            removeIcon.addClickListener(event -> this.removeFromParent());
            price.setPrefixComponent(new Paragraph("$"));
            usdtAmount.setPrefixComponent(new Paragraph("$"));
            percent.setSuffixComponent(new Paragraph("%"));
            price.setMin(0.0);
            percent.setMin(0);
            percent.setMax(100);
        }

        public boolean isSelected() {
            return markAsBought.getValue();
        }

    }

}
