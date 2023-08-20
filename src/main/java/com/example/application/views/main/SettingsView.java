package com.example.application.views.main;

import com.example.application.views.main.components.tabs.PasswordTab;
import com.example.application.views.main.components.tabs.ProfileTab;
import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
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

    private final Tab profile;
    private final Tab password;
    private final Tab notifications;
    private final VerticalLayout content;

    public SettingsView() {
        addClassName("page-content");
        profile = new Tab("Profile");
        notifications = new Tab("Profile");
        password = new Tab("Password");

        Tabs tabs = new Tabs(profile, password, notifications);
        tabs.addSelectedChangeListener(
                event -> setContent(event.getSelectedTab()));

        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        content = new VerticalLayout();
        setContent(tabs.getSelectedTab());

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.add(tabs, content);
        hLayout.setSpacing(true);

        add(hLayout);
    }

    private void setContent(Tab tab) {
        content.removeAll();

        if (tab.equals(profile)) {
            content.add(new ProfileTab());
        } else if (tab.equals(password)) {
            content.add(new PasswordTab());
        } else {
            content.add(new Paragraph("This is the Notifications tab"));
        }
    }

}
