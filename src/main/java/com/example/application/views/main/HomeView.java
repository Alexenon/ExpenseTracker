package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends Main {

    public HomeView() {
        addClassName("page-content");
        Paragraph text = new Paragraph("Welcome to Home page!");
        add(text);
    }
}




