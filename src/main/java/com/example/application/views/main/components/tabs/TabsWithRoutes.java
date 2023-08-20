package com.example.application.views.main.components.tabs;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Settings")
@Route(value = "settings")
@ParentLayout(MainLayout.class)
public class TabsWithRoutes extends HorizontalLayout implements RouterLayout, BeforeEnterObserver {

    public TabsWithRoutes() {
        addClassName("page-content");

        RouteTabs routeTabs = new RouteTabs();
        routeTabs.add(new RouterLink("Profile", ProfileTab.class));
        routeTabs.add(new RouterLink("Notifications", NotificationTab.class));
        routeTabs.add(new RouterLink("Password", PasswordTab.class));

        Icon icon = new Icon("lumo", "cog");
        H2 title = new H2("Settings");

        Div titleContainer = new Div();
        titleContainer.add(icon, title);

        HorizontalLayout layout = new HorizontalLayout();
        layout.add(routeTabs);

        add(titleContainer, layout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getNavigationTarget() == TabsWithRoutes.class) {
            event.forwardTo(ProfileTab.class);
        }
    }

}