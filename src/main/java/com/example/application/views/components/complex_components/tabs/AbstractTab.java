package com.example.application.views.components.complex_components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;

public abstract class AbstractTab extends Tab {

    public AbstractTab() {
        super();
    }

    public AbstractTab(String name) {
        super(name);
    }

    public abstract Div getContent();

}
