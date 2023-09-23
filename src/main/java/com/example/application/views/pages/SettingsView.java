package com.example.application.views.pages;

import com.example.application.views.components.complex_components.tabs.NotificationTab;
import com.example.application.views.components.complex_components.tabs.PasswordTab;
import com.example.application.views.components.complex_components.tabs.ProfileTab;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends Main implements HasDynamicTitle {

    private final TabSheet tabSheet;
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
    public String getPageTitle() {
        return tabSheet.getSelectedTab().getLabel();
    }

    private void customizeTabSheet() {
        tabSheet.setId("settings-tabsheet");

        Tab profileTab = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
        Tab notificationsTab = new Tab(VaadinIcon.BELL.create(), new Span("Notifications"));
        Tab passwordTab = new Tab(VaadinIcon.LOCK.create(), new Span("Password"));

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
        });
    }

    /**
     * Changes the URL in the browser, but doesn't reload the page.
     */
    private void updatePageUrl(String s) {
        String deepLinkingUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
        UI.getCurrent().getPage().getHistory().replaceState(null, deepLinkingUrl + s);
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