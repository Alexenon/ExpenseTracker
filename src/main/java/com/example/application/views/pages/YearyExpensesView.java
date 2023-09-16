package com.example.application.views.pages;

import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Yearly expenses")
@Route(value = "yearly", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/expensesAllTime.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class YearyExpensesView extends Main {

    YearyExpensesView() {
        addClassName("page-content");
        HorizontalLayout container = new HorizontalLayout();
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Div chartPie = new Div();
        chartPie.setId("yearly-chart");

        UI.getCurrent().getPage().executeJs("fillChart()");

        container.add(chartPie);
        add(container);
    }

}
