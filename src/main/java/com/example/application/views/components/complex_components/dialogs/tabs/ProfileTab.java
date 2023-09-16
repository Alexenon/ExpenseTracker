package com.example.application.views.main.components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "settings/profile", layout = TabsWithRoutes.class)
public class ProfileTab extends Div {

    Paragraph text = new Paragraph("This is the Profile tab");

    public ProfileTab() {
        add(text);
    }
}
