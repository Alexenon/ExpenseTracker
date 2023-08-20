package com.example.application.views.main.components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

public class ProfileTab extends Div {

    Paragraph text = new Paragraph("This is the Profile tab");

    public ProfileTab() {
        add(text);
    }
}
