package com.example.application.views.main;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        addHeader();
        addFooter();
    }


    private void addHeader() {
        Text text = new Text("This is a header");

        HorizontalLayout header = new HorizontalLayout(text);
        header.setId("header");
        header.setSizeFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Div navbar = new Div();
        navbar.setId("navbar");
        navbar.add(new RouterLink("Home", MainView.class));
        navbar.add(new RouterLink("Dashboard", DashboardView.class));

        header.add(navbar);
        addToNavbar(header);
    }

    private void addFooter() {
        Text text = new Text("This is a footer");

        HorizontalLayout footer = new HorizontalLayout(text);
        footer.setWidth("100%");
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        // Missing method to add footer like for header
    }

}
