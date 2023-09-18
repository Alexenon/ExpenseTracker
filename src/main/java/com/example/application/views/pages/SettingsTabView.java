package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Test Settings")
@Route(value = "test", layout = MainLayout.class)
public class SettingsTabView extends Main  implements RouterLayout {

    private final VerticalLayout content;

    public SettingsTabView() {
        addClassName("page-content");

        Tab profileTab = new Tab("Profile");
        Tab notificationTab = new Tab("Notifications");
        Tab passwordTab = new Tab("Password");

        Tabs tabs = new Tabs(profileTab, notificationTab, passwordTab);

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        content = new VerticalLayout();

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.add(tabs, content);

        add(hLayout);
    }
}
