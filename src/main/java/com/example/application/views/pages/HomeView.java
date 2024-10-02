package com.example.application.views.pages;

import com.example.application.views.components.complex_components.icons.MonoIcon;
import com.example.application.views.components.complex_components.icons.PictogramIcon;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;

@AnonymousAllowed
@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class HomeView extends Main {

    public HomeView() {
        addClassName("page-content");
        Paragraph paragraph = new Paragraph("Welcome to Home page!");
        add(paragraph);

        Text text = new Text("Bob lives in a");

        Icon lumoIcon = LumoIcon.BELL.create();
        MonoIcon pictogramIcon = PictogramIcon.LOGIN.create();
        MonoIcon monoIcon = new MonoIcon(PictogramIcon.LOGOUT);

        Div div = new Div(text, lumoIcon, pictogramIcon, monoIcon);

        add(div);

    }
}




