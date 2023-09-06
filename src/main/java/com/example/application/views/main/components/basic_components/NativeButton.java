package com.example.application.views.main.components.basic_components;

import com.vaadin.flow.component.*;

import java.util.Optional;

@Tag("button")
public class NativeButton extends HtmlContainer implements ClickNotifier<NativeButton>, Focusable<NativeButton>, HasAriaLabel {

    private static final PropertyDescriptor<String, Optional<String>> typeDescriptor = PropertyDescriptors.optionalAttributeWithDefault("type", "");

    public NativeButton() {
    }

    public NativeButton(String text) {
        this.setText(text);
    }

    public NativeButton(String text, ComponentEventListener<ClickEvent<NativeButton>> clickListener) {
        this(text);
        this.addClickListener(clickListener);
    }

    public void setType(String type) {
        this.set(typeDescriptor, type);
    }

}