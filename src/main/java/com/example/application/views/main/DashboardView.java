package com.example.application.views.main;

import com.example.application.service.ExpenseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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

        HorizontalLayout container = new HorizontalLayout();
        container.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div chartPie = new Div();
        chartPie.setId("chart-pie");
        chartPie.setWidth("600px");
        chartPie.setHeight("600px");

        UI.getCurrent().getPage().executeJs("fillChartPie()");
        UI.getCurrent().getPage().executeJs("showChartDetails('//div[@id=\"chart-pie\"]')");
        UI.getCurrent().getPage().executeJs("printMe('http://localhost:8080/api/getAll')");

        container.add(chartPie);
        add(container);
    }

    public String getPieChartData() {
        return convertToJson(expenseService.getAllExpenses());
    }

    private <T> String convertToJson(List<T> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }



}
