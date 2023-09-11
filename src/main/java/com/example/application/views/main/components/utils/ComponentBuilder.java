package com.example.application.views.main.components.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

public class ComponentBuilder {

    private Component component;

    public ComponentBuilder initialize(Component component) {
        this.component = component;
        return this;
    }

    public ComponentBuilder addClass(String className) {
        if (component != null) {
            component.addClassNames(className);
        }
        return this;
    }

    public ComponentBuilder add(Component childComponent) {
        if (component instanceof HasComponents) {
            ((HasComponents) component).add(childComponent);
        }
        return this;
    }

    public ComponentBuilder add(Component ... childComponents) {
        if (component instanceof HasComponents) {
            ((HasComponents) component).add(childComponents);
        }
        return this;
    }

    public Component build() {
        return component;
    }
}
