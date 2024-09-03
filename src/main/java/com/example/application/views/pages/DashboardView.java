package com.example.application.views.pages;

import com.example.application.data.dtos.projections.ExpensesSumGroupedByCategory;
import com.example.application.services.ExpenseService;
import com.example.application.services.SecurityService;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends Main {

    private final SecurityService securityService;
    private final ExpenseService expenseService;

    private String selectedCategory;

    public DashboardView(SecurityService securityService, ExpenseService expenseService) {
        this.securityService = securityService;
        this.expenseService = expenseService;
        addStyle();
    }

    private void addStyle() {
        addClassName("page-content");

        Div chartPie = new Div();
        chartPie.setId("chart-pie");
        initMonthlyExpensesChart();

        Paragraph selectedCategoryField = new Paragraph();


        // TODO: Display table with selected category
        chartPie.getElement().addEventListener("click", event -> chartPie.getElement()
                .executeJs("return this.getAttribute('data-selected');")
                .then(String.class, selectedValue -> {
                    if (selectedValue == null || selectedValue.isEmpty() || selectedValue.equals(selectedCategory)) {
                        return;
                    }

                    selectedCategory = selectedValue;
                    selectedCategoryField.setText(selectedCategory);
                }));

        HorizontalLayout container = new HorizontalLayout(chartPie);
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        add(container, selectedCategoryField);
    }

    private void initMonthlyExpensesChart() {
        String username = securityService.getAuthenticatedUser().getUsername();
        List<ExpensesSumGroupedByCategory> sumGroupedByCategory = expenseService.getExpensesTotalSumGroupedByCategory(username);

        JsonArray jsonOptionData = Json.createArray();
        for (int i = 0; i < sumGroupedByCategory.size(); i++) {
            JsonObject jsonObject = Json.createObject();
            ExpensesSumGroupedByCategory item = sumGroupedByCategory.get(i);

            System.out.println("name: " + item.getCategoryName());
            System.out.println("value: " + item.getTotalSpentPerMonth().orElse(0.0));

            jsonObject.put("name", item.getCategoryName());
            jsonObject.put("value", item.getTotalSpentPerMonth().orElse(0.0));
            jsonOptionData.set(i, jsonObject);
        }

        System.out.println("Dashboard Data -> " + jsonOptionData.toJson());
        UI.getCurrent().getPage().executeJs("fillChartPie($0);", jsonOptionData.toJson());
    }


}
