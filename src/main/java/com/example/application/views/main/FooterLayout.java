package com.example.application.views.main;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RouterLayout;

@ParentLayout(MainLayout.class)
public class FooterLayout extends VerticalLayout implements RouterLayout {

    private final Div container = new Div();

    public FooterLayout() {
        Div footer = new Div();
        add(container, footer);
        footer.add(new Text("I'm a fixed footer!"));
        add(footer);
    }

    public void showRouterLayoutContent(HasElement content) {
        container.removeAll();
        container.getElement().setChild(0, content.getElement());
    }
}

//https://www.youtube.com/watch?v=-J4RZXfgLRc
//https://stackoverflow.com/questions/68248661/vaadin-14-6-applayout-with-statusbar
