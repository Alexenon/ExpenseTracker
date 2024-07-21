package com.example.application.views.components.native_components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

@Tag(Tag.DIV)
public class Container extends Div {

    public Container() {
        super();
    }

    public Container(Component... components) {
        super(components);
    }

    public Container(String className) {
        this();
        addClassName(className);
    }

    public Container(String className, Component... components) {
        this(components);
        addClassName(className);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class to create easier the nested containers
     */
    public static class Builder {
        private final Container instance;

        public Builder() {
            this.instance = new Container();
        }

        public Builder addComponent(Component component) {
            instance.add(component);
            return this;
        }

        public Builder addComponents(Component... components) {
            instance.add(components);
            return this;
        }

        public Builder removeComponent(Component component) {
            instance.remove(component);
            return this;
        }

        public Builder removeFromParent() {
            instance.removeFromParent();
            return this;
        }

        public Builder addClassName(String className) {
            instance.addClassName(className);
            return this;
        }

        public Builder addClassNames(String... classNames) {
            instance.addClassNames(classNames);
            return this;
        }

        public Builder removeClassName(String className) {
            instance.removeClassName(className);
            return this;
        }

        public Builder removeClassNames(String... classNames) {
            instance.removeClassNames(classNames);
            return this;
        }

        public Container build() {
            return instance;
        }

    }

}
