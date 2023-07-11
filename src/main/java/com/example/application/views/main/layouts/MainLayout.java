package com.example.application.views.main.layouts;

import com.example.application.views.main.DashboardView;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

public class MainLayout extends AppLayout {

    public MainLayout() {
        addHeader();
        addFooter();
    }

    private void addHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.setSizeFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Div navbar = new Div();
        navbar.setId("navbar");

        Div innerMenu = new Div();
        innerMenu.setClassName("menu-inner");

        Span text = new Span("LOGO");
        text.addClassName("logo");

        Icon personIcon = new Icon(VaadinIcon.USER);
        personIcon.addClassName("margin-end");

        final RouterLink home = new RouterLink("Home", MainView.class);
        final RouterLink dashboard = new RouterLink("Dashboard", DashboardView.class);
        final RouterLink statistics = new RouterLink("Statistics", DashboardView.class);
        final RouterLink contact = new RouterLink("Contact", DashboardView.class);

        List<Component> listOfRoutes = List.of(home, dashboard, statistics, contact);
        listOfRoutes.forEach(e -> e.setClassName("menu-item"));
        innerMenu.add(listOfRoutes);

        navbar.add(text, innerMenu);
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
