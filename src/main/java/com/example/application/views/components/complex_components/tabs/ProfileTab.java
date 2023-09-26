package com.example.application.views.components.complex_components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import jakarta.annotation.security.PermitAll;

@PermitAll
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
