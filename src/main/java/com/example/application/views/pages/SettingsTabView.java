package com.example.application.views.pages;

import com.example.application.views.components.complex_components.tabs.NotificationTab;
import com.example.application.views.components.complex_components.tabs.PasswordTab;
import com.example.application.views.components.complex_components.tabs.ProfileTab;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Test Settings")
@Route(value = "test", layout = MainLayout.class)
public class SettingsTabView extends Main {

    private final TabSheet tabSheet;
    private final Div profileTabContent;
    private final Div notificationsTabContent;
    private final Div passwordTabContent;

    public SettingsTabView() {
        addClassNames("page-content", "settings-page");

        tabSheet = new TabSheet();
        profileTabContent = new ProfileTab();
        notificationsTabContent = new NotificationTab();
        passwordTabContent = new PasswordTab();

        customizeTabSheet();
        placeTabsheetVertical();

        H2 pageTitle = new H2("Settings");
        pageTitle.addComponentAsFirst(new Icon(VaadinIcon.COG));

        Div pageContainer = new Div(pageTitle, tabSheet);

        add(pageContainer);
    }

    private void customizeTabSheet() {
        tabSheet.setId("settings-tabsheet");

        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
        Tab notificationsTab = new Tab(VaadinIcon.BELL.create(), new Span("Notifications"));
        Tab passwordTab = new Tab(VaadinIcon.LOCK.create(), new Span("Password"));

        tabSheet.add(profileTab, profileTabContent);
        tabSheet.add(notificationsTab, notificationsTabContent);
        tabSheet.add(passwordTab, passwordTabContent);
    }

    private void placeTabsheetVertical() {
        String script = """
                const tabsheet = document.getElementById('settings-tabsheet');
                const vaadinTabs = tabsheet.querySelector('vaadin-tabs');
                vaadinTabs.setAttribute('orientation', 'vertical');
                """;

        UI.getCurrent().getPage().executeJs(script);

        tabSheet.getStyle().set("display", "grid");
        tabSheet.getStyle().set("grid-template-columns", "1fr 4fr");
        tabSheet.getStyle().set("grid-gap", "20px");
    }

}
