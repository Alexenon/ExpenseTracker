package com.example.application.views.components.complex_components;

import com.example.application.views.pages.*;
import com.example.application.views.pages.crypto.comparator.AssetCompareView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

/*
    TODO: Add Contact Page
* */

@Tag(Tag.NAV)
public class NavigationBar extends Nav {

    private final Image logoImage = new Image("/images/logos/logo-white-background.png", "Logo image");
    private final RouterLink expensesLink = new RouterLink("Expenses", ExpensesView.class);
    private final RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
    private final RouterLink yearlyLink = new RouterLink("Yearly", YearyExpensesView.class);
    private final RouterLink comparatorLink = new RouterLink("Comparator", AssetCompareView.class);
    private final RouterLink assetsLink = new RouterLink("Portfolio", PortfolioTrackerView.class);

    public NavigationBar() {
        addClassName("navbar");

        logoImage.addClassNames("logo");
        logoImage.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

        Div innerMenu = new Div();
        innerMenu.addClassName("menu-inner");
        innerMenu.add(logoImage);
        innerMenu.add(getRoutes());
        innerMenu.getChildren().forEach(c -> c.addClassName("menu-item"));

        Div menu = new Div(innerMenu);
        menu.setClassName("menu");

        add(menu);
    }

    public List<Component> getRoutes() {
        return List.of(expensesLink, dashboardLink, yearlyLink, assetsLink, comparatorLink);
    }

    public Image getLogo() {
        return logoImage;
    }

}
