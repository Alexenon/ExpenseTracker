package com.example.application.views.components.basic_components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextFieldBase;
import lombok.Getter;

@Tag(Tag.DIV)
public class LabeledInput<T extends TextFieldBase<T, ?>> extends Div {

    @Getter
    private final Label labelField;
    @Getter
    private final T inputField;

    public LabeledInput(String labelName, T inputField) {
        labelField = new Label(labelName);
        this.inputField = inputField;

        add(labelField, this.inputField);
    }

    public void setInputId(String id) {
        inputField.setId(id);
        labelField.setFor(id);
    }

}
