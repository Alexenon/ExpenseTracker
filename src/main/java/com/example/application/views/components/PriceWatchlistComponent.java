package com.example.application.views.components;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.StringUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.CurrencyField;
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
 * TODO: LONG TERM:
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

    /**
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private class WatchlistLayout extends Div {

        // TODO:
        //  - Add "unsaved" / "draft" status
        //  - Binder validation should be triggered after save, and not on changing

        private final AssetWatcher assetWatcher;
        private final Binder<AssetWatcher> binder = new Binder<>(AssetWatcher.class);

        private final Paragraph status = new Paragraph();
        private final CurrencyField target = new CurrencyField("Price");
        private final CurrencyField targetAmount = new CurrencyField("Amount in USD");
        private final Checkbox markAsCompleted = new Checkbox("Mark as completed");
        private final Button saveBtn = new Button("Save");
        private final Button deleteBtn = new Button("Delete");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());

        private boolean isEditMode;

        /**
         * Default constructor, that is used when creating a watchlistLayout without any data
         * to be retrieved from the database, with the input fields should be filled and saved.
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

            saveBtn.addClickListener(event -> {
                binder.validate();
                if (binder.writeBeanIfValid(assetWatcher)) {
                    System.out.println("Saving " + assetWatcher);
                    instrumentsFacadeService.saveAssetWatcher(assetWatcher);
                    setEditMode(false);
                } else {
                    System.out.println("Validation failed.");
                }
            });

            markAsCompleted.addClickListener(e -> updateStatus());
            deleteBtn.addClickListener(event -> this.removeFromParent());

            editBtn.addClickListener(event -> {
                setEditMode(!isEditMode);
                revertChanges();
            });

            initBinder();
            setEditMode(isEditMode);
            updateStatus();
        }

        private Div buildBody() {
            return Container.builder()
                    .addClassName("card-wrapper-body")
                    .addComponent(target)
                    .addComponent(targetAmount)
                    .addComponent(markAsCompleted)
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
            markAsCompleted.setVisible(isEditMode);
            saveBtn.setVisible(isEditMode);
            deleteBtn.setVisible(isEditMode);
            editBtn.setIcon(isEditMode ? LumoIcon.UNDO.create() : LumoIcon.EDIT.create());
        }

        // TODO: Make same for percentage, this is for price
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

            binder.forField(markAsCompleted)
                    .bind(AssetWatcher::isCompleted, AssetWatcher::setCompleted);

            binder.setValidatorsDisabled(true);
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
            status.setText(markAsCompleted.getValue() ? "Ended" : "Ongoing");
            status.removeClassNames("completed", "ongoing");
            status.addClassName(markAsCompleted.getValue() ? "completed" : "ongoing");
        }

    }


}
