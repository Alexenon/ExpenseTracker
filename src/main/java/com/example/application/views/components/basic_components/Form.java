package com.example.application.views.components.basic_components;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;

import java.util.Optional;

@Tag("form")
public class Form extends HtmlContainer {

    private static final PropertyDescriptor<String, Optional<String>> actionDescriptor = PropertyDescriptors.optionalAttributeWithDefault("action", "");
    private static final PropertyDescriptor<String, Optional<String>> methodDescriptor = PropertyDescriptors.optionalAttributeWithDefault("method", "");

    public Form() {
        super("form");
    }

    public void setAction(String action) {
        this.set(actionDescriptor, action);
    }

    public void setMethod(String method) {
        this.set(methodDescriptor, method);
    }

}




