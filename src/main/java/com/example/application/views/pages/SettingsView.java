package com.example.application.views.pages;

import com.example.application.views.components.complex_components.tabs.NotificationTab;
import com.example.application.views.components.complex_components.tabs.PasswordTab;
import com.example.application.views.components.complex_components.tabs.ProfileTab;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "settings", layout = MainLayout.class)
@RouteAlias(value = "settings/notifications", layout = MainLayout.class)
@RouteAlias(value = "settings/password", layout = MainLayout.class)
public class SettingsView extends AbstractPage implements HasDynamicTitle, BeforeEnterObserver {

    private final TabSheet tabSheet;

    private final Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
    private final Tab notificationsTab = new Tab(VaadinIcon.BELL.create(), new Span("Notifications"));
    private final Tab passwordTab = new Tab(VaadinIcon.LOCK.create(), new Span("Password"));

    private final Div profileTabContent;
    private final Div notificationsTabContent;
    private final Div passwordTabContent;

    public SettingsView() {
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
        pageContainer.addClassName("page-container");

        add(pageContainer);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String url = beforeEnterEvent.getLocation().getPath();
        String urlSuffix = url.substring(url.lastIndexOf("/") + 1);

        Tab selectedTab;
        switch (urlSuffix) {
            case "notifications" -> selectedTab = notificationsTab;
            case "password" -> selectedTab = passwordTab;
            default -> selectedTab = profileTab;
        }

        tabSheet.setSelectedTab(selectedTab);
        updatePageTitle(urlSuffix);
    }

    @Override
    public String getPageTitle() {
        return tabSheet.getSelectedTab().getLabel();
    }

    private void customizeTabSheet() {
        tabSheet.setId("settings-tabsheet");

        profileTab.setClassName("profile-tab");
        notificationsTab.setClassName("notifications-tab");
        passwordTab.setClassName("password-tab");

        tabSheet.add(profileTab, profileTabContent);
        tabSheet.add(notificationsTab, notificationsTabContent);
        tabSheet.add(passwordTab, passwordTabContent);

        tabSheet.addSelectedChangeListener(selectedChangeEvent -> {
            String urlSuffix = tabSheet.getSelectedIndex() == 0
                    ? ""
                    : "/" + tabSheet.getSelectedTab().getClassName().replace("-tab", "");

            updatePageUrl(urlSuffix);
            updatePageTitle(urlSuffix);
        });
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