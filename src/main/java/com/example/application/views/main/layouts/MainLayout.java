package com.example.application.views.main.layouts;

import com.example.application.views.main.components.complex_components.Navbar;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainLayout extends AppLayout {

    public MainLayout() {
        addHeader();
        addFooter();
    }

    private void addHeader() {
        Header header = new Header();
        header.setId("header");
        header.setSizeFull();

        Navbar navbar = new Navbar();
        Icon personIcon = new Icon(VaadinIcon.USER);
        personIcon.addClassName("margin-end");

        header.add(navbar, personIcon);
        addToNavbar(header);
    }

    private void addFooter() {
        Text text = new Text("This is a footer");

        HorizontalLayout footer = new HorizontalLayout(text);
        footer.setWidth("100%");
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }

}
