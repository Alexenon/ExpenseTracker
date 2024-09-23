package com.example.application.views.components;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.StringUtils;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/*
 * TODO:
 *  - Add percentage alternative
 *  - Add style for 'Save' and 'Delete' buttons
 * */

public class PriceWatchlistComponent extends Div {

    private final Asset asset;
    private final AssetWatcher.ActionType actionType;
    private final InstrumentsFacadeService instrumentsFacadeService;

    private final List<AssetWatcher> assetWatchers;

    private final Div priceLayoutContainer = new Div();

    @Autowired
    public PriceWatchlistComponent(Asset asset, AssetWatcher.ActionType actionType, InstrumentsFacadeService instrumentsFacadeService) {
        this.asset = asset;
        this.actionType = actionType;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.assetWatchers = instrumentsFacadeService.getAssetWatchersByAssetAndActionType(asset, actionType);

        initialize();
        fillComponent();
    }

    private void initialize() {
        add(priceLayoutContainer);
    }

    private void fillComponent() {
        if (assetWatchers.isEmpty()) {
            addNewPriceLayout();
        } else {
            assetWatchers.forEach(assetWatcher -> priceLayoutContainer.add(new WatchlistLayout(assetWatcher)));
        }
    }

    public void addNewPriceLayout() {
        priceLayoutContainer.add(new WatchlistLayout());
    }

    /*
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private class WatchlistLayout extends Div {

        private final AssetWatcher assetWatcher;
        private final Binder<AssetWatcher> binder = new Binder<>(AssetWatcher.class);

        private final Paragraph status = new Paragraph();
        private final CurrencyField target = new CurrencyField("Price");
        private final CurrencyField targetAmount = new CurrencyField("Amount in USD");
        private final Checkbox markAsBought = new Checkbox("Mark as bought");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());
        private final Button saveBtn = new Button("Save");
        private final Button deleteBtn = new Button("Delete");

        private boolean isEditMode;

        /**
         * Default constructor, that is used when creating a watchlistLayout without any data
         * to be retrieved from the database, and the component fields should be filled and saved.
         */
        public WatchlistLayout() {
            this.isEditMode = true;
            this.assetWatcher = new AssetWatcher();
            this.assetWatcher.setAsset(asset);
            this.assetWatcher.setTargetType(AssetWatcher.TargetType.PRICE);
            this.assetWatcher.setActionType(actionType);
            init();
        }

        public WatchlistLayout(AssetWatcher assetWatcher) {
            this.assetWatcher = assetWatcher;
            init();
        }

        private void init() {
            addClassName("section-card-wrapper");
            addClassName("price-watcher-wrapper");

            // Scrolls smoothly to the center of newly created element
            UI.getCurrent().getPage()
                    .executeJs("arguments[0].scrollIntoView({ behavior: 'smooth', block: 'center' });", this);

            Container content = Container.builder("price-watcher-card-content")
                    .addComponent(status)
                    .addComponent(buildBody())
                    .addComponent(buildFooter())
                    .build();

            add(content, editBtn);

            target.setLabel(StringUtils.uppercaseFirstLetter(actionType.name()) + " " + target.getLabel());
            target.setClassName("asset-amount-field");
            targetAmount.setClassName("asset-amount-field");
            status.setClassName("watchlist-status");

            saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            editBtn.addClickListener(event -> {
                setEditMode(!isEditMode);
                revertChanges();
            });

            markAsBought.addClickListener(e -> updateStatus());

            saveBtn.addClickListener(event -> {
                if (binder.writeBeanIfValid(assetWatcher)) {
                    System.out.println("Saving " + assetWatcher);
                    instrumentsFacadeService.saveAssetWatcher(assetWatcher);
                    setEditMode(false);
                } else {
                    System.out.println("Validation failed.");
                }
            });

            deleteBtn.addClickListener(event -> this.removeFromParent());

            initBinder();
            setEditMode(isEditMode);
            updateStatus();
        }

        private Div buildBody() {
            return Container.builder()
                    .addClassName("card-wrapper-body")
                    .addComponent(target)
                    .addComponent(targetAmount)
                    .addComponent(markAsBought)
                    .build();
        }

        private Div buildFooter() {
            return Container.builder()
                    .addClassName("card-wrapper-footer")
                    .addComponent(saveBtn)
                    .addComponent(deleteBtn)
                    .build();
        }

        public void setEditMode(boolean editMode) {
            isEditMode = editMode;
            target.setReadOnly(!isEditMode);
            targetAmount.setReadOnly(!isEditMode);
            status.setVisible(!isEditMode);
            markAsBought.setVisible(isEditMode);
            saveBtn.setVisible(isEditMode);
            deleteBtn.setVisible(isEditMode);
            editBtn.setIcon(isEditMode ? LumoIcon.UNDO.create() : LumoIcon.EDIT.create());
        }

        // This is for price
        // TODO: Make same for percentage
        private void initBinder() {
            binder.forField(target)
                    .asRequired("Please fill this field")
                    .withConverter(new StringToDoubleConverter(0.0, "Couldn't convert to double"))
                    .withValidator(amount -> amount > 0, "Target should be bigger than 0")
                    .bind(AssetWatcher::getTarget, AssetWatcher::setTarget);

            binder.forField(targetAmount)
                    .asRequired("Please fill this field")
                    .withConverter(new StringToDoubleConverter(0.0, "Couldn't convert to double"))
                    .withValidator(amount -> amount > 0, "Amount should be bigger than 0")
                    .bind(AssetWatcher::getTargetAmount, AssetWatcher::setTargetAmount);

            binder.forField(markAsBought)
                    .bind(AssetWatcher::isCompleted, AssetWatcher::setCompleted);

            // Initially load the bean into the form
            binder.readBean(assetWatcher);
        }

        /**
         * Resetting form to have old values on cancel action
         */
        private void revertChanges() {
            binder.readBean(assetWatcher);
            updateStatus();
        }

        private void updateStatus() {
            status.setText(markAsBought.getValue() ? "Ended" : "Ongoing");
            status.removeClassNames("completed", "ongoing");
            status.addClassName(markAsBought.getValue() ? "completed" : "ongoing");
        }

    }


}
