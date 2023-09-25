package com.example.application.views.components.complex_components.tabs;

import com.example.application.views.pages.SettingsView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
//@Route(value = "settings/profile", layout = SettingsView.class)
public class ProfileTab extends Div {

    Paragraph text = new Paragraph("This is the Profile tab");

    public ProfileTab() {
        add(text);

        for (int i = 0; i < 10; i++) {
            add(content(i));
        }

    }

    private Div content(int i) {
        Paragraph p = new Paragraph("Paragraph " + i);
        return new Div(p);
    }
}
