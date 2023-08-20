package com.example.application.views.main;

import com.example.application.views.main.components.tabs.AbstractTab;
import com.example.application.views.main.components.tabs.NotificationTab;
import com.example.application.views.main.components.tabs.PasswordTab;
import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends Main {

    private final VerticalLayout content;

    public SettingsView() {
        addClassName("page-content");

        Tab profileTab = new PasswordTab();
        Tab notificationTab = new NotificationTab();
        Tab passwordTab = new PasswordTab();

        Tabs tabs = new Tabs(profileTab, notificationTab, passwordTab);

        tabs.addSelectedChangeListener(
                event -> setContent(event.getSelectedTab()));

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        content = new VerticalLayout();
        setContent(tabs.getSelectedTab());

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.add(tabs, content);

        add(hLayout);
    }

    private void setContent(Tab tab) {
        AbstractTab abstractTab = (AbstractTab) tab;
        content.removeAll();
        content.add(abstractTab.getContent());
    }

}
