package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
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
        Paragraph paragraph = new Paragraph("Welcome to Home page!");
        add(paragraph);
    }
}




