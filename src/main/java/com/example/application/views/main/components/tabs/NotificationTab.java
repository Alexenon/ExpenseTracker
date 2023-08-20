package com.example.application.views.main.components.tabs;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

public class NotificationTab extends AbstractTab {

    public NotificationTab() {
        super("Notifications");
    }

    @Override
    public Div getContent() {
        Div div = new Div();
        Paragraph p1 = new Paragraph("This is the Notifications tab");
        Paragraph p2 = new Paragraph("This is another paragraph");
        div.add(p1, p2);
        return div;
    }
}
