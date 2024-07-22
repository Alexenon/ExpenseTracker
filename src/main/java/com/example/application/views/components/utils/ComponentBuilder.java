package com.example.application.views.components.utils;

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
            component.addClassName(className);
        }
        return this;
    }

    public ComponentBuilder add(Component childComponent) {
        if (component instanceof HasComponents) {
            ((HasComponents) component).add(childComponent);
        }
        return this;
    }

    public ComponentBuilder add(Component... childComponents) {
        if (component instanceof HasComponents) {
            ((HasComponents) component).add(childComponents);
        }
        return this;
    }

    public Component build() {
        return component;
    }

    @FunctionalInterface
    public interface Customizer<T> {
        static <T> Customizer<T> withDefaults() {
            return (t) -> {
            };
        }

        void customize(T t);
    }

}

