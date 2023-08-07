package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Yearly expenses")
@Route(value = "yearly", layout = MainLayout.class)
public class YearyExpensesView extends Main {

    YearyExpensesView() {
        addClassName("page-content");
        Button button = new Button("Show",
                event -> System.out.println("Clicked")
        );
        button.setId("button-left");
        add(button);
    }

}
