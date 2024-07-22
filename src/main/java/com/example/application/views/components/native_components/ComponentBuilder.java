package com.example.application.views.components.native_components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

import java.util.function.Consumer;

public class ComponentBuilder<T extends Component> {

    private final T instance;
    private Consumer<T> instanceConsumer;

    public ComponentBuilder(Class<T> clazz) {
        try {
            this.instance = clazz.getDeclaredConstructor().newInstance();
            this.instanceConsumer = (component) -> component = instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create component instance", e);
        }
    }

    public static void main(String[] args) {
        // TODO: FIX THE BUG
        Component parentDiv = new ComponentBuilder<>(Div.class)
                .add(childDiv -> {
                    childDiv.addClassName("child");
                    childDiv.add(new Paragraph("Some text"));
                })
                .build();

        parentDiv.addClassName("parent");
    }

    public ComponentBuilder<T> add(Consumer<T> consumer) {
        instanceConsumer = instanceConsumer.andThen(consumer);
        return this;
    }

    public T build() {
        instanceConsumer.accept(instance);
        return instance;
    }
}