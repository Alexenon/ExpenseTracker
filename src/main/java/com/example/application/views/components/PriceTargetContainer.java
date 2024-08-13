package com.example.application.views.components;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.InstrumentsService;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/*
 * TODO:
 *  - Add mark as completed style -> maybe 'completed' class
 *  - Add percentage alternative
 *  - Cancel button should return the old values - taken from fields -> NOT WORKING
 *  - Add style for 'Save' and 'Delete' buttons
 * */

public class PriceTargetContainer extends Div {

    private final Asset asset;
    private final InstrumentsService instrumentsService;

    private final Div priceLayoutContainer = new Div();
    private final Button addBtn = new Button(LumoIcon.PLUS.create());

    @Getter
    @Setter
    private List<AssetWatcher> targets;

    @Autowired
    public PriceTargetContainer(Asset asset, InstrumentsService instrumentsService) {
        this.asset = asset;
        this.instrumentsService = instrumentsService;
        this.targets = instrumentsService.getAssetWatchersByAsset(asset);

        initialize();
        fillComponent();
    }

    private void initialize() {
        add(addBtn, priceLayoutContainer);
        addBtn.addClickListener(event -> priceLayoutContainer.add(new PriceLayout()));
    }

    private void fillComponent() {
        if (targets.isEmpty()) {
            priceLayoutContainer.add(new PriceLayout());
        } else {
            targets.forEach(assetWatcher -> priceLayoutContainer.add(new PriceLayout(assetWatcher)));
        }
    }


    /*
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private class PriceLayout extends Div {

        private final AssetWatcher assetWatcher;
        private final Binder<AssetWatcher> binder = new Binder<>(AssetWatcher.class);

        private final NumberField target = new NumberField("Buy Price");
        private final NumberField targetAmount = new NumberField("Amount in USD");
        private final Checkbox markAsBought = new Checkbox("Mark as bought");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());
        private final Button saveBtn = new Button("Save");
        private final Button deleteBtn = new Button("Delete");

        private boolean isEditMode;

        /**
         * Default constructor, that is used when no data is retrieved from database,
         * and the component fields should be filled
         */
        public PriceLayout() {
            this.isEditMode = true;
            this.assetWatcher = new AssetWatcher();
            this.assetWatcher.setAsset(asset);
            this.assetWatcher.setTargetType(AssetWatcher.TargetType.PRICE);
            this.assetWatcher.setActionType(AssetWatcher.ActionType.BUY);
            init();
        }

        public PriceLayout(AssetWatcher assetWatcher) {
            this.assetWatcher = assetWatcher;
            init();
        }

        private void init() {
            addClassName("section-card-wrapper");
            addClassName("price-watcher-wrapper");

            saveBtn.addClickListener(event -> {
                if (binder.writeBeanIfValid(assetWatcher)) {
                    System.out.println("Saving " + assetWatcher);
                    instrumentsService.saveAssetWatcher(assetWatcher);
                    setEditMode(false);
                } else {
                    System.out.println("Validation failed.");
                }
            });
            deleteBtn.addClickListener(event -> this.removeFromParent());
            editBtn.addClickListener(event -> {
                setEditMode(!isEditMode);
                revertChanges();
            });

            Container content = Container.builder("price-watcher-card-content")
                    .addComponent(buildBody())
                    .addComponent(buildFooter())
                    .build();

            add(content, editBtn);

            saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

            target.setClassName("asset-amount-field");
            targetAmount.setClassName("asset-amount-field");
            target.setPrefixComponent(new Paragraph("$"));
            targetAmount.setPrefixComponent(new Paragraph("$"));

            initBinder();
            setEditMode(isEditMode);
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
            markAsBought.setVisible(isEditMode);
            saveBtn.setVisible(isEditMode);
            deleteBtn.setVisible(isEditMode);
            editBtn.setIcon(isEditMode ? LumoIcon.CROSS.create() : LumoIcon.EDIT.create());
        }

        private void initBinder() {
            // This is for price
            // TODO: Make same for percentage
            binder.forField(target)
                    .asRequired("Please fill this field")
                    .withValidator(amount -> amount.longValue() >= 0, "Target should be greater or equal to 0")
                    .bind(AssetWatcher::getTarget, AssetWatcher::setTarget);

            binder.forField(targetAmount)
                    .asRequired("Please fill this field")
                    .withValidator(amount -> amount.longValue() >= 0, "Amount should be greater or equal to 0")
                    .bind(AssetWatcher::getTargetAmount, AssetWatcher::setTargetAmount);

            binder.forField(markAsBought)
                    .bind(AssetWatcher::isCompleted, AssetWatcher::setCompleted);

            // Initially load the bean into the form
            binder.readBean(assetWatcher);
        }

        /**
         * Resetting form to have old values on cancel action
         * */
        private void revertChanges() {
            binder.readBean(assetWatcher);
            target.setValue(assetWatcher.getTarget());
            targetAmount.setValue(assetWatcher.getTargetAmount());
        }

    }


}
