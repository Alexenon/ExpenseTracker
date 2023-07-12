package com.example.application.views.main.layouts;

import com.example.application.views.main.DashboardView;
import com.example.application.views.main.ExpensesView;
import com.example.application.views.main.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
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

        Image logo = new Image("images/logo_with_color.svg", "Logo image");
        logo.addClassName("logo");

        final RouterLink homeLink = new RouterLink("Home", HomeView.class);
        final RouterLink expensesLink = new RouterLink("Expenses", ExpensesView.class);
        final RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
        final RouterLink contactLink = new RouterLink("Contact", DashboardView.class);

        List<Component> listOfRoutes = List.of(homeLink, expensesLink, dashboardLink, contactLink);
        listOfRoutes.forEach(e -> e.setClassName("menu-item"));
        innerMenu.add(listOfRoutes);

        navbar.add(logo, innerMenu);
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
