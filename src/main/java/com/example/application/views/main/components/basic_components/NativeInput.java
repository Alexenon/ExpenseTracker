package com.example.application.views.main.components.basic_components;

import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Input;

import java.util.Optional;

@Tag(Tag.INPUT)
public class NativeInput extends Input {

    private static final PropertyDescriptor<String, Optional<String>> nameDescriptor = PropertyDescriptors.optionalAttributeWithDefault("name", "");

    public NativeInput() {
    }

    public NativeInput(String type) {
        this.setType(type);
    }

    public NativeInput(String type, String placeholder) {
        this.setType(type);
        this.setPlaceholder(placeholder);
    }

    public NativeInput(String name, String type, String placeholder) {
        this.setType(type);
        this.setPlaceholder(placeholder);
        this.setName(name);
    }

    public void setName(String name) {
        this.set(nameDescriptor, name);
    }

}
