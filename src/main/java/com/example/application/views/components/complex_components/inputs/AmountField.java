package com.example.application.views.components.complex_components.inputs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.List;

/**
 * Field that supports an amount as:
 *  - price equivalent -> '$'
 *  - percentage equivalent -> '%'
 */
public class AmountField extends Div {

    private static final List<String> ALL_OPTIONS = List.of("$", "%");

    private final NumberField inputField = new NumberField();
    private final Select<String> options = new Select<>();

    public AmountField() {
        inputField.setMin(0.0);
        setPriceField();

        options.setItems(ALL_OPTIONS);
        options.addValueChangeListener(e -> {
            switch (e.getValue()) {
                case "$" -> setPriceField();
                case "%" -> setPercentageField();
                default -> throw new IllegalArgumentException("Not supported " + e.getValue());
            }
        });

        add(inputField, options);
    }

    private void setPriceField() {
        inputField.setMax(Double.MAX_VALUE);
        inputField.setPrefixComponent(new Paragraph("$"));
        inputField.setSuffixComponent(null);
    }

    private void setPercentageField() {
        inputField.setMax(100);
        inputField.setPrefixComponent(null);
        inputField.setSuffixComponent(new Paragraph("%"));
    }


}
