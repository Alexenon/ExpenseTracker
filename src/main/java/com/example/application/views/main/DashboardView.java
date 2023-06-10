package com.example.application.views.main;

import com.example.application.service.ExpenseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule(value = "./themes/mytodo/components/fillChart.js")
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/echarts/4.6.0/echarts-en.min.js")
public class DashboardView extends HorizontalLayout {

    private final ExpenseService expenseService;

    @Autowired
    public DashboardView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div div = new Div();
        div.setId("chart-pie");
        div.setWidth("500px");
        div.setHeight("500px");
        add(div);

        UI.getCurrent().getPage().executeJs("fillChartPie()");
    }

}
