package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.crypto.conversion.ImportDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("test")
@Route(value = "/test", layout = MainLayout.class)
public class TestView extends AbstractPage {

    public TestView() {
        getStyle().set("margin-top", "100px");
        Button button = new Button("Import");
        button.addClickListener(e -> {
            ImportDialog dialog = new ImportDialog();
            dialog.open();
        });
        add(button);
    }
}
