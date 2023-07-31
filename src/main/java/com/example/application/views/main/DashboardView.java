package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
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
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends Main {

    public DashboardView() {
        addStyle();
    }

    private void addStyle() {
        addClassName("page-content");
        HorizontalLayout container = new HorizontalLayout();
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Div chartPie = new Div();
        chartPie.setId("chart-pie");

        UI.getCurrent().getPage().executeJs("fillChartPie('http://localhost:8080/api/expense/grouped?user=test')");
        UI.getCurrent().getPage().executeJs("printMe('http://localhost:8080/api/expense/all')");

        container.add(chartPie);
        add(container);
    }

}
