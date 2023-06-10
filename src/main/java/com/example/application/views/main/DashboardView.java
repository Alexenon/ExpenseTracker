package com.example.application.views.main;

import com.example.application.service.ExpenseService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;


/* https://codesandbox.io/s/mogry5?file=/index.js:0-988 */
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/mytodo/components/dashboard.html")
public class DashboardView extends HorizontalLayout {

    private final ExpenseService expenseService;

    @Autowired
    public DashboardView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
    }



}
