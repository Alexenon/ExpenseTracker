package com.example.application.views.main.components.complex_components;

import com.example.application.views.main.DashboardView;
import com.example.application.views.main.ExpensesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

@Tag("navbar")
public class Navbar extends Div {

    private final Image logo = new Image("images/logo_with_color.svg", "Logo image");
    private final RouterLink expensesLink = new RouterLink("Expenses", ExpensesView.class);
    private final RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
    private final RouterLink contactLink = new RouterLink("Contact", DashboardView.class);

    public Navbar() {
        Div navbarLayout = new Div();
        navbarLayout.addClassName("menu-inner");
        navbarLayout.add(getRoutes());
        add(navbarLayout);

        addStyle();
    }

    private void addStyle() {
        getRoutes().forEach(e -> e.setClassName("menu-item"));
        logo.addClassNames("logo");
    }

    public List<Component> getRoutes() {
        return List.of(logo, expensesLink, dashboardLink, contactLink);
    }

}
