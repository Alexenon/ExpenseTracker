package com.example.application.views.components;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.StringUtils;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/*
 * TODO: [LONG TERM]:
 *  - Add percentage alternative
 *  - Add style for 'Save' and 'Delete' buttons
 * */

public class PriceWatchlistComponent extends Div implements HasNotifications {

    private final Asset asset;
    private final AssetWatcher.ActionType actionType;
    private final InstrumentsFacadeService instrumentsFacadeService;

    private final List<AssetWatcher> userWatchers;

    private final Div priceLayoutContainer = new Div();

    @Autowired
    public PriceWatchlistComponent(Asset asset, AssetWatcher.ActionType actionType, InstrumentsFacadeService instrumentsFacadeService) {
        this.asset = asset;
        this.actionType = actionType;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.userWatchers = instrumentsFacadeService.getAssetWatchersByAssetAndActionType(asset, actionType);

        add(priceLayoutContainer);
        fillComponent();
    }

    private void fillComponent() {
        if (userWatchers.isEmpty()) {
            addNewPriceLayout();
        } else {
            userWatchers.forEach(assetWatcher -> priceLayoutContainer.add(new WatchlistLayout(assetWatcher)));
        }
    }

    public void addNewPriceLayout() {
        priceLayoutContainer.add(new WatchlistLayout());
    }

    /**
     * PriceLayout used for tracking wanted sell/buy prices
     */
    private class WatchlistLayout extends Div {

        // TODO:
        //  - Binder validation should be triggered after save, and not on changing

        private final AssetWatcher assetWatcher;
        private final Binder<AssetWatcher> binder = new Binder<>(AssetWatcher.class);
        private final Paragraph status = new Paragraph();
        private final CurrencyField target = new CurrencyField("Price");
        private final CurrencyField targetAmount = new CurrencyField("Amount in USD");
        private final Checkbox markAsCompleted = new Checkbox("Mark as completed");
        // FIXME: Invalid vaadin checkbox version, most probably broken by Add On
        private final Container checkboxContainer = new Container("centered-row", markAsCompleted, new Span("Mark as completed"));
//        private final DualLabelToggleButton toggleBtn = new DualLabelToggleButton("$", "%");
        private final Button saveBtn = new Button("Save");
        private final Button deleteBtn = new Button("Delete");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());

        private boolean isDraft;
        private boolean isEditMode;

        /**
         * Default constructor, that is used when creating a watchlistLayout without any data
         * to be retrieved from the database, with the input fields should be filled and saved.
         */
        public WatchlistLayout() {
            this.isDraft = true;
            this.isEditMode = false;
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
            addClassNames("section-card-wrapper", "price-watcher-wrapper");

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

//            toggleBtn.addClickListener(e -> {
//                System.out.println(toggleBtn.isChecked());
//            });

            saveBtn.addClickListener(event -> {
                System.out.println("isDraft = " + isDraft);
                binder.validate();
                if (binder.writeBeanIfValid(assetWatcher)) {
                    System.out.println("Saving " + assetWatcher);
                    instrumentsFacadeService.saveAssetWatcher(assetWatcher);
                    isDraft = false;
                    updateStatus();
                    setEditMode(false);
                    showSuccessfulNotification("Sucessfully saved");
                } else {
                    showErrorNotification("Something went wrong");
                }
                System.out.println("isDraft = " + isDraft);
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
            return new Container("card-wrapper-body", target, targetAmount,
//                    toggleBtn,  // TODO: Add me again and continue styling
                    checkboxContainer);
        }

        private Div buildFooter() {
            return new Container("card-wrapper-footer", saveBtn, deleteBtn);
        }

        public void setEditMode(boolean editMode) {
            isEditMode = editMode;
            target.setReadOnly(!isEditMode);
            targetAmount.setReadOnly(!isEditMode);
            status.setVisible(!isEditMode);
            checkboxContainer.setVisible(isEditMode);
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

            // Initially load the bean into the form
            binder.readBean(assetWatcher);
            binder.setValidatorsDisabled(true);
        }

        /**
         * Resetting form to have old values on cancel action
         */
        private void revertChanges() {
            binder.readBean(assetWatcher);
            updateStatus();
        }

        private void updateStatus() {
            String newStatus = retrieveStatus();
            status.removeClassNames("draft", "completed", "ongoing");
            status.setText(newStatus);
            status.addClassName(newStatus.toLowerCase());
        }

        private String retrieveStatus() {
            return isDraft ? "Draft" : markAsCompleted.getValue() ? "Completed" : "Ongoing";
        }

    }


}
