package com.example.application.views.components.complex_components.tabs;

import com.example.application.views.components.complex_components.tabs.TabsWithRoutes;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "settings/notifications", layout = TabsWithRoutes.class)
public class NotificationTab extends Div {

    public NotificationTab() {
        add(getContent());
    }

    public Div getContent() {
        Div div = new Div();
        Paragraph p1 = new Paragraph("This is the Notifications tab");
        Paragraph p2 = new Paragraph("This is another paragraph");
        div.add(p1, p2);
        return div;
    }
}
