package com.example.application.views.components.complex_components;

import com.example.application.views.pages.DashboardView;
import com.example.application.views.pages.ExpensesView;
import com.example.application.views.pages.HomeView;
import com.example.application.views.pages.YearyExpensesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

@Tag(Tag.NAV)
public class NavigationBar extends Nav {

    private final Image logoImage = new Image("/images/logos/logo-white-background.png", "Logo image");
    private final RouterLink expensesLink = new RouterLink("Expenses", ExpensesView.class);
    private final RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
    private final RouterLink contactLink = new RouterLink("Yearly", YearyExpensesView.class);

    public NavigationBar() {
        addClassName("navbar");
        Div innerMenu = new Div();
        innerMenu.addClassName("menu-inner");
        innerMenu.add(getAllRoutes());

        Div menu = new Div(innerMenu);
        menu.setClassName("menu");

        add(menu);
        addStyle();
    }

    private void addStyle() {
        getAllRoutes().forEach(e -> e.setClassName("menu-item"));
        logoImage.addClassNames("logo");
        logoImage.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));
    }

    public List<Component> getAllRoutes() {
        return List.of(logoImage, expensesLink, dashboardLink, contactLink);
    }

    public List<Component> getTextLinks() {
        return List.of(expensesLink, dashboardLink, contactLink);
    }

}
