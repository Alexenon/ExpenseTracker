package com.example.application.views.components.core;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;

public class ComponentBuilder<T extends Component> {

    private final T component;

    public ComponentBuilder(T component) {
        this.component = component;
    }

    public ComponentBuilder(Class<T> clazz) {
        try {
            this.component = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create component instance", e);
        }
    }

    public ComponentBuilder<T> addClass(String className) {
        if (component != null) {
            component.addClassName(className);
        }
        return this;
    }

    public ComponentBuilder<T> add(Component childComponent) {
        if (component instanceof HasComponents allowed) {
            allowed.add(childComponent);
        }
        return this;
    }

    public ComponentBuilder<T> add(Component... childComponents) {
        if (component instanceof HasComponents allowed) {
            allowed.add(childComponents);
        }
        return this;
    }

    public ComponentBuilder<T> setText(String text) {
        if (component instanceof HasText allowed) {
            allowed.setText(text);
        }
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ComponentBuilder<T> addClickListener(ComponentEventListener<ClickEvent<T>> clickListener) {
        if (component instanceof ClickNotifier allowed) {
            allowed.addClickListener(clickListener);
        }
        return this;
    }

    public T build() {
        return component;
    }

    public static void main(String[] args) {
        Paragraph component = new ComponentBuilder<>(Paragraph.class)
                .setText("Text")
                .addClass("my-class")
                .add(new Span("my-span"))
                .build();

        Paragraph component2 = new ComponentBuilder<>(new Paragraph("Text"))
                .addClass("my-class")
                .add(new Span("my-span"))
                .build();


        System.out.println(component);
    }

}

