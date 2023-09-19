package com.example.application.views.components.complex_components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class SettingsTabSheet extends Tabs {

    Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
    Tab notificationsTab = new Tab(VaadinIcon.BELL.create(), new Span("Notifications"));
    Tab passwordTab = new Tab(VaadinIcon.COG.create(), new Span("Password"));

    Div profileTabContent;
    Div notificationsTabContent;
    Div passwordTabContent;

    private final VerticalLayout content;

    public SettingsTabSheet() {
        profileTabContent = new ProfileTab();
        notificationsTabContent = new NotificationTab();
        passwordTabContent = new PasswordTab();
        content = new VerticalLayout();

        add(profileTab, notificationsTab, passwordTab);
        addSelectedChangeListener((e) -> {
            Tab selectedTab = e.getSelectedTab();

        });
    }

    public Div getSelectedTabContent() {
        Tab selectedTab = getSelectedTab();

        if (selectedTab == profileTab) {
            return profileTabContent;
        } else if (selectedTab == notificationsTab) {
            return notificationsTabContent;
        }
        return passwordTabContent;
    }

}
