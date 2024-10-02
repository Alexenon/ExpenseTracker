package com.example.application.views.layouts;

import com.example.application.services.SecurityService;
import com.example.application.views.components.complex_components.NavigationBar;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    private final NavigationBar navigationBar = new NavigationBar();
    private final Header header = new Header(navigationBar);

    @Autowired
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        build();
    }

    private void build() {
        header.setId("header");

        Icon personIcon = new Icon(VaadinIcon.USER);
        personIcon.addClassName("user-icon");
        personIcon.addClickListener(e -> securityService.logout());

        navigationBar.add(personIcon);

        addToNavbar(header);
        addFooter();
    }

    private void addFooter() {
        Text text = new Text("This is a footer");

        HorizontalLayout footer = new HorizontalLayout(text);
        footer.setWidth("100%");
        footer.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }

}
