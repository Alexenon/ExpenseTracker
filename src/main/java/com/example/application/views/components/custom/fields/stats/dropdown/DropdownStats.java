package com.example.application.views.components.custom.fields.stats.dropdown;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;

public class DropdownStats extends Div {

    private final Button displayBtn = new Button();
    private final Div displayContainer;

    private boolean isOpened = false;

    public DropdownStats(String labelText, Div displayContainer) {
        this.displayBtn.setText(labelText);
        this.displayContainer = displayContainer;
        initialize();
    }

    private void initialize() {
        displayBtn.setIcon(LumoIcon.ANGLE_DOWN.create());
        displayBtn.setIconAfterText(true);
        displayBtn.getStyle().set("position", "relative");
        displayBtn.addClickListener(e -> {
            isOpened = !isOpened;
            displayBtn.setIcon(getIcon());
            displayContainer.setVisible(isOpened);
        });

        displayContainer.addClassName("popup-display-item");
        displayContainer.getStyle()
                .set("position", "absolute")
                .set("width", "max-content")
                .set("height", "fit-content")
                .set("transform", "translateY(8px)")
                .set("animation", "fromTopToBottom_animation .2s ease-in");

        add(displayBtn, displayContainer);
    }

    private Icon getIcon() {
        return isOpened ? LumoIcon.ANGLE_UP.create() : LumoIcon.ANGLE_DOWN.create();
    }

}
