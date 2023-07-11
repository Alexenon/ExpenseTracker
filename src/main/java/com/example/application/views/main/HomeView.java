package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        Text text = new Text("Welcome to Home page!");
        add(text);
    }
}




