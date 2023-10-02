package com.example.application.views.components.basic_components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
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
        this.labelField = new Label(labelName);
        this.inputField = inputField;

        add(labelField, this.inputField);
    }

    public void setInputId(String id) {
        inputField.setId(id);
        labelField.setFor(id);
    }

    public void setNativeInputAttribute(String attribute, String value) {
        String id = inputField.getId()
                .orElseThrow(() -> new IllegalArgumentException("Input field should have an id"));

        String script = String.join("\n",
                "const field = document.getElementById('%s');".formatted(id),
                "const input = field.querySelector('input');",
                "input.setAttribute('%s', '%s');".formatted(attribute, value)
        );

        UI.getCurrent().getPage().executeJs(script);
    }

}
