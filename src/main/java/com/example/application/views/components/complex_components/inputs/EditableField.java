package com.example.application.views.components.complex_components.inputs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import lombok.Data;

/**
 * Component used to edit, display and change data
 * using a simple input
 */
public class EditableField extends Div {

    private Component inputField;
    private Component displayField;
    private boolean isEditMode;

    public EditableField(Component inputField, Component displayField) {
        this(inputField, displayField, true);
    }

    public EditableField(Component inputField, Component displayField, boolean isEditMode) {
        this.inputField = inputField;
        this.displayField = displayField;
        this.isEditMode = isEditMode;
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;

    }

}
