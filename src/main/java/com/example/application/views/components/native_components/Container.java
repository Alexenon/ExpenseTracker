package com.example.application.views.components.native_components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.dom.Element;

import java.util.function.Function;
import java.util.function.Supplier;

@Tag(Tag.DIV)
public class Container extends Div implements FlexComponent {

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

    public static Builder builder(String className) {
        return new Builder(className);
    }

    /**
     * Builder class to create easier the nested containers
     */
    public static class Builder {
        private final Container instance;

        public Builder() {
            this.instance = new Container();
        }

        public Builder(String className) {
            this.instance = new Container(className);
        }

        public Builder addElement(Element element) {
            instance.getElement().appendChild(element);
            return this;
        }

        public Builder addComponent(Component component) {
            instance.add(component);
            return this;
        }

        public Builder addComponent(Supplier<Component> componentSupplier) {
            Component component = componentSupplier.get();
            instance.add(component);
            return this;
        }

        public <T extends Component> Builder addComponent(Function<Container, T> componentSupplier) {
            T component = componentSupplier.apply(instance);
            instance.add(component);
            return this;
        }

        public Builder addComponents(Component... components) {
            instance.add(components);
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

        public Builder setDisplay(String type) {
            instance.getStyle().set("display", type);
            return this;
        }

        public Builder setAlignItems(FlexComponent.Alignment alignment) {
            instance.setAlignItems(alignment);
            return this;
        }

        public Builder setAlignSelf(FlexComponent.Alignment alignment) {
            instance.setAlignSelf(alignment);
            return this;
        }

        public Builder setStyle(String property, String value) {
            instance.getStyle().set(property, value);
            return this;
        }

        public Builder setGap(String value) {
            instance.getStyle().set("gap", value);
            return this;
        }

        public Container build() {
            return instance;
        }

    }

}
