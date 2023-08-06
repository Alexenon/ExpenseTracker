package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Yearly expenses")
@JsModule("./themes/light_theme/components/javascript/fillChart.js")
@JsModule("./themes/light_theme/components/javascript/test.js")
@Route(value = "yearly", layout = MainLayout.class)
public class YearyExpensesView extends Main {

    YearyExpensesView() {
        addClassName("page-content");
        Button button = new Button("Show",
                event -> UI.getCurrent().getPage().executeJs("callMe()")
        );
        add(button);
    }

}
