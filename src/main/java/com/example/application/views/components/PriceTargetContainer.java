package com.example.application.views.components;

import com.example.application.data.models.watcher.PriceTarget;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
 * TODO:
 *  - Add second class and check how css will work
 *  - Place icons on top right corner (try position: relative)
 *  - Add footer
 *  - Add percentage alternative
 *      - Add
 *      - Think about adding $ and % signs
 *  - Think displaying no information
 *  - Cancel button should return old values - take from field
 * */

public class PriceTargetContainer extends Div {

    private final Div priceLayoutContainer = new Div();
    private final Button addBtn = new Button(LumoIcon.PLUS.create());

    @Getter
    @Setter
    private List<PriceTarget> targets;

    public PriceTargetContainer() {
        this(new ArrayList<>());
        priceLayoutContainer.add(new PriceLayout(true));
        initialize();
    }

    public PriceTargetContainer(List<PriceTarget> targets) {
        this.targets = targets;
        initialize();
    }

    private void initialize() {
        add(addBtn, priceLayoutContainer);
        addBtn.addClickListener(event -> priceLayoutContainer.add(new PriceLayout(true)));
    }

    /*
     * PriceLayout used for tracking wanted sell/buy prices
     * */
    private class PriceLayout extends Div {

        private final NumberField targetPrice = new NumberField("Buy Price");
        private final NumberField targetAmount = new NumberField("Amount in USD");
        private final Checkbox markAsBought = new Checkbox("Mark as bought");
        private final Button editBtn = new Button(LumoIcon.EDIT.create());

        private final Button saveBtn = new Button("Save");
        private final Button deleteBtn = new Button("Delete");
        private final Button cancelBtn = new Button("Cancel");

        private boolean isEditMode;

        public PriceLayout(boolean isEditMode) {
            this.isEditMode = isEditMode;
            init();
        }

        // TODO: Make field, and edit it in the process of modification
        public PriceLayout(PriceTarget priceWatcher) {
            init();
            targetPrice.setValue(priceWatcher.getTarget());
            targetAmount.setValue(priceWatcher.getTargetAmount());
        }

        private void init() {
            setClassName("section-card-wrapper");

            editBtn.addClickListener(event -> setEditMode(true));
            deleteBtn.addClickListener(event -> this.removeFromParent());
            saveBtn.addClickListener(event -> {
                targets.add(getPriceWatcher());
//                updateItems();
                setEditMode(false);
            });
            cancelBtn.addClickListener(event -> setEditMode(false));

            add(
                    editBtn,
                    buildBody(),
                    buildFooter()
            );

            editBtn.setVisible(!isEditMode);
            markAsBought.setVisible(isEditMode);
            deleteBtn.setVisible(isEditMode);
            saveBtn.setVisible(isEditMode);
            cancelBtn.setVisible(isEditMode);

            targetPrice.setMin(0.0);
            targetAmount.setMin(0.0);
            saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        }

        public void setEditMode(boolean editMode) {
            isEditMode = editMode;
            targetPrice.setReadOnly(!isEditMode);
            targetAmount.setReadOnly(!isEditMode);
            editBtn.setVisible(!isEditMode);
            markAsBought.setVisible(isEditMode);
            saveBtn.setVisible(isEditMode);
            deleteBtn.setVisible(isEditMode);
            cancelBtn.setVisible(isEditMode);
        }

        private Div buildBody() {
            return Container.builder()
                    .addClassName("card-wrapper-body")
                    .addComponent(targetPrice)
                    .addComponent(targetAmount)
                    .addComponent(markAsBought)
                    .build();
        }

        private Div buildFooter() {
            return Container.builder()
                    .addClassName("card-wrapper-footer")
                    .addComponent(saveBtn)
                    .addComponent(deleteBtn)
                    .addComponent(cancelBtn)
                    .build();
        }

        public PriceTarget getPriceWatcher() {
            double targetAmountValue = targetAmount.getValue();
            double targetPriceValue = targetPrice.getValue();
            return new PriceTarget(targetAmountValue, targetPriceValue);
        }

    }


}
