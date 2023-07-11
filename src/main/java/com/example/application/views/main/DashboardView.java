package com.example.application.views.main;

import com.example.application.services.ExpenseService;
import com.example.application.views.main.layouts.MainLayout;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;

import java.util.List;
import java.util.Optional;

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule(value = "./themes/mytodo/components/fillChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends HorizontalLayout {

    private final ExpenseService expenseService;

    public DashboardView(@Autowired ExpenseService expenseService) {
        this.expenseService = expenseService;
        setDefaultVerticalComponentAlignment(Alignment.CENTER);

        HorizontalLayout container = new HorizontalLayout();
        container.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div chartPie = new Div();
        chartPie.setId("chart-pie");
        chartPie.setWidth("700px");
        chartPie.setHeight("500px");

        UI.getCurrent().getPage().executeJs("fillChartPie()");
        UI.getCurrent().getPage().executeJs("printMe('http://localhost:8080/api/expense/all')");

        container.add(chartPie);
        add(container);
    }

    public String getPieChartData() {
        return convertToJson(expenseService.getAllExpenses())
                .orElseThrow(JsonParseException::new);
    }

    private <T> Optional<String> convertToJson(List<T> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
