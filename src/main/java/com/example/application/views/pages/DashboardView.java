package com.example.application.views.pages;

import com.example.application.data.dtos.projections.MonthlyExpensesProjection;
import com.example.application.data.dtos.projections.TotalMonthlyExpensesSumGroupedByCategory;
import com.example.application.services.ExpenseService;
import com.example.application.services.SecurityService;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
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

import java.time.LocalDate;
import java.util.List;

/*
 * TODO: Integrate userSecurity service directly into the expense service
 *  - Add filter by excluded category
 * */

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends Main {

    private final SecurityService securityService;
    private final ExpenseService expenseService;

    private final Div chartPie = new Div();
    private final Grid<MonthlyExpensesProjection> grid = new Grid<>();
    private final GridListDataView<MonthlyExpensesProjection> dataView = grid.setItems();

    Paragraph selectedCategoryField = new Paragraph();

    public DashboardView(SecurityService securityService, ExpenseService expenseService) {
        this.securityService = securityService;
        this.expenseService = expenseService;
        initialize();
        initializeGrid();
        initializeChart();
    }

    private void initialize() {
        addClassName("page-content");

        HorizontalLayout container = new HorizontalLayout(chartPie, grid);
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        add(container, selectedCategoryField);
    }

    private void initializeGrid() {
        chartPie.setId("chart-pie");

        String username = securityService.getAuthenticatedUser().getUsername();
        grid.setItems(expenseService.getMonthlyExpenses(username, LocalDate.now()));

        grid.addColumn(MonthlyExpensesProjection::getName).setKey("Expense Name").setHeader("Expense Name");
        grid.addColumn(MonthlyExpensesProjection::getCategoryName).setKey("Category Name").setHeader("Category Name");
        grid.addColumn(MonthlyExpensesProjection::getTimestamp).setKey("Interval").setHeader("Interval");
        grid.addColumn(MonthlyExpensesProjection::getAmount).setKey("Amount").setHeader("Amount");
        grid.addColumn(MonthlyExpensesProjection::getTimesTriggered).setKey("Times Triggered").setHeader("Times Triggered");
        grid.addColumn(MonthlyExpensesProjection::getStartDate).setKey("Start Date").setHeader("Start Date");
        grid.addColumn(MonthlyExpensesProjection::getEndDate).setKey("End Date").setHeader("End Date");

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);

        chartPie.getElement().addEventListener("click", event -> filterBySelectedCategory());
    }

    private void filterBySelectedCategory() {
        chartPie.getElement()
                .executeJs("return this.getAttribute('data-selected');")
                .then(String.class, selectedCategoryName -> {
                    if (selectedCategoryName == null || selectedCategoryName.isEmpty()) {
                        selectedCategoryField.setText("Unselected");
                        dataView.setFilter(e -> true);
                        return;
                    }

                    selectedCategoryField.setText(selectedCategoryName);
                    dataView.setFilter(projection -> projection.getCategoryName().equalsIgnoreCase(selectedCategoryName));
                });
    }

    private void initializeChart() {
        String username = securityService.getAuthenticatedUser().getUsername();
        List<TotalMonthlyExpensesSumGroupedByCategory> sumGroupedByCategory = expenseService.getTotalSpentPerMonthByCategory(username);

        JsonArray jsonOptionData = Json.createArray();
        for (int i = 0; i < sumGroupedByCategory.size(); i++) {
            JsonObject jsonObject = Json.createObject();
            TotalMonthlyExpensesSumGroupedByCategory item = sumGroupedByCategory.get(i);

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
