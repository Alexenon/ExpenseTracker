package com.example.application.views.main.layouts;

import com.example.application.services.SecurityService;
import com.example.application.views.main.ProfileView;
import com.example.application.views.main.SettingsView;
import com.example.application.views.main.components.complex_components.NavigationBar;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainLayout extends AppLayout {

    public MainLayout() {
        addHeader();
        addFooter();
    }

    private void addHeader() {
        addToNavbar(getHeader());
    }

    private Header getHeader() {
        Header header = new Header();
        header.setId("header");

        Icon personIcon = new Icon(VaadinIcon.USER);
        personIcon.addClassName("user-icon");
        personIcon.addClickListener(e -> {
            SecurityService securityService = new SecurityService();
            securityService.logout();
        });

        Icon settingsIcon = new Icon(VaadinIcon.COG);
        settingsIcon.addClassName("user-icon");
        settingsIcon.addClickListener(e -> UI.getCurrent().navigate(SettingsView.class));

        NavigationBar navbar = new NavigationBar();
        navbar.add(settingsIcon, personIcon);
        header.add(navbar);

        return header;
    }

    private void addFooter() {
        Text text = new Text("This is a footer");

        HorizontalLayout footer = new HorizontalLayout(text);
        footer.setWidth("100%");
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }

}
