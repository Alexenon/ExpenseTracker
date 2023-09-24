package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends AbstractPage {

    public HomeView() {
        addClassName("page-content");
        Paragraph text = new Paragraph("Welcome to Home page!");
        add(text);
    }
}




