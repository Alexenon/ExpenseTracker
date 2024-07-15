package com.example.application.views.components.complex_components.dialogs;

import com.example.application.data.models.Asset;
import com.example.application.data.models.Currency;
import com.example.application.data.models.Holding;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.entities.AssetWatcher;
import com.example.application.entities.User;
import com.example.application.entities.WatchPrice;
import com.example.application.services.AssetWatcherService;
import com.example.application.services.SecurityService;
import com.example.application.services.UserService;
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

public class AddAssetDialog extends Dialog implements HasNotifications {

    private final AssetWatcherService assetWatcherService;
    private final SecurityService securityService;
    private final UserService userService;

    private final Select<String> nameField = new Select<>();
    private final TextArea commentField = new TextArea("Comments");
    private final Div wrapper = new Div();
    private final PriceLayer priceLayer = new PriceLayer();

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    private final InstrumentsProvider instrumentsProvider;


    public AddAssetDialog(AssetWatcherService assetWatcherService,
                          SecurityService securityService,
                          UserService userService) {
        this.assetWatcherService = assetWatcherService;
        this.securityService = securityService;
        this.userService = userService;
        this.instrumentsProvider = InstrumentsProvider.getInstance();

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
        nameField.setLabel("Token");
        nameField.setItems(instrumentsProvider.getCurrencyList().stream().map(Currency::getName).toList());
        nameField.setHelperText("Select the crypto currency you want to add");

        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(e -> this.close());

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> defaultClickSaveBtnListener());

        this.getFooter().add(cancelButton, saveButton);
    }

    private void defaultClickSaveBtnListener() {
//        AssetWatcher assetWatcher = getAssetWatcher();
//        assetWatcherService.save(assetWatcher);
        this.close();
    }

    private AssetWatcher getAssetWatcher() {
        String currencyName = nameField.getValue();
        List<WatchPrice> watchPrices = priceLayer.getPrices().stream().map(WatchPrice::new).toList();
        String comment = commentField.getValue();
        User user = userService.findByUsernameIgnoreCase("test").orElse(null);
//        String username = securityService.getAuthenticatedUserDetails().getUsername();

        return new AssetWatcher(currencyName, watchPrices, comment, user);
    }

    public Asset getAsset() {
        Currency currency = instrumentsProvider.getCurrencyByName(nameField.getValue());
        List<Holding> holdings = new ArrayList<>();
        String comment = commentField.getValue();
        return new Asset(currency.getName(), holdings, comment);
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
            NumberField amountField = new NumberField("Amount");
            amountField.setSuffixComponent(new Span("USDT"));
            amountField.getStyle().set("margin-bottom", "1rem");
            amountField.setMin(0);

            Button addButton = new Button("Add");
            addButton.addClickListener(e -> addNewContainer());

            Button removeButton = new Button("Remove");
            Div subContainer = new Div(amountField, addButton, removeButton);
            removeButton.addClickListener(e -> wrapper.remove(subContainer));

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
