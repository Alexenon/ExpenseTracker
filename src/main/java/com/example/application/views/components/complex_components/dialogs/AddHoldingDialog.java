package com.example.application.views.components.complex_components.dialogs;

import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AddHoldingDialog extends Dialog implements HasNotifications {

    private final Select<String> nameField = new Select<>();
    private final TextArea commentField = new TextArea("Comments");
    private final Div wrapper = new Div();
    private final PriceLayer priceLayer = new PriceLayer();

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    public AddHoldingDialog() {
        add(createDialogLayout());
        addStyleToElements();
    }

    private VerticalLayout createDialogLayout() {
        Component[] components = {nameField, priceLayer, commentField};
        VerticalLayout dialogLayout = new VerticalLayout(components);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "22rem").set("max-width", "100%");
        Arrays.stream(components).forEach(e -> e.getStyle().set("margin-bottom", "1rem"));

        return dialogLayout;
    }

    private void addStyleToElements() {
        List<String> cryptoCurrencyNames = List.of("BTC", "ETH", "SOL", "TON", "NOT");

        nameField.setLabel("Token");
        nameField.setItems(cryptoCurrencyNames);
        nameField.setHelperText("Select the crypto currency you want to buy");

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> {
            String text = priceLayer.getPrices()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n"));
            commentField.setValue(text);
        });

        this.getFooter().add(cancelButton, saveButton);
    }

    private void defaultClickSaveBtnListener() {
        // Save the holding
    }

    public void addClickSaveBtnListener(Consumer<?> listener) {
        saveButton.addClickListener(e -> listener.accept(null));
    }

    private class PriceLayer extends Div {

        public PriceLayer() {
            addNewContainer();
            add(wrapper);
        }

        private void addNewContainer() {
            Div subContainer = new Div();

            NumberField amountField = new NumberField("Amount");
            amountField.setSuffixComponent(new Span("USDT"));
            amountField.getStyle().set("margin-bottom", "1rem");

            Button addButton = new Button("Add");
            addButton.addClickListener(e -> addNewContainer());

            Button removeButton = new Button("Remove");
            removeButton.addClickListener(e -> wrapper.remove(subContainer));

            subContainer.add(amountField, addButton, removeButton);
            wrapper.add(subContainer);
        }

        public List<Double> getPrices() {
            List<Double> prices = new ArrayList<>();
            wrapper.getChildren().forEach(component -> {
                if (component instanceof Div subContainer) {
                    subContainer.getChildren().forEach(child -> {
                        if (child instanceof NumberField amountField) {
                            Double value = amountField.getValue();
                            if (value != null) {
                                prices.add(value);
                            }
                        }
                    });
                }
            });
            return prices;
        }

    }

}
