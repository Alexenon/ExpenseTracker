package com.example.application.views.pages;

import com.example.application.data.dtos.projections.MonthlyExpensesProjection;
import com.example.application.services.ExpenseService;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/*
 * TODO: Integrate userSecurity service directly into the expense service
 * */

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends Main {

    private final ExpenseService expenseService;

    private final Div chartPie = new Div();
    private final Grid<MonthlyExpensesProjection> grid = new Grid<>();
    private final GridListDataView<MonthlyExpensesProjection> dataView = grid.setItems();

    private final AtomicReference<String> selectedCategoryName = new AtomicReference<>();
    private final AtomicReference<List<String>> legendHiddenCategories = new AtomicReference<>();

    @Autowired
    public DashboardView(ExpenseService expenseService) {
        this.expenseService = expenseService;
        initialize();
        initializeGrid();
        initializeChart();


        List<MonthlyExpensesProjection> all = expenseService.getMonthlyExpensesByUser(LocalDate.now());


        all.stream()
                .collect(Collectors.groupingBy(
                        MonthlyExpensesProjection::getCategoryName,
                        Collectors.summingDouble(p -> p.getAmount() * p.getTimesTriggered())
                )).forEach((category, total) ->
                        System.out.println("Category: " + category + ", Total Spent: " + total)
                );


        double total = all.stream().mapToDouble(p -> p.getTimesTriggered() * p.getAmount()).sum();
        System.out.println("Total = " + total);


    }

    private void initialize() {
        addClassName("page-content");

        HorizontalLayout container = new HorizontalLayout(chartPie, grid);
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        add(container);
    }

    private void initializeGrid() {
        chartPie.setId("chart-pie");

        grid.setItems(expenseService.getMonthlyExpensesByUser(LocalDate.now()));
        grid.addColumn(MonthlyExpensesProjection::getName).setKey("Expense Name").setHeader("Expense Name");
        grid.addColumn(MonthlyExpensesProjection::getCategoryName).setKey("Category Name").setHeader("Category Name");
        grid.addColumn(MonthlyExpensesProjection::getTimestamp).setKey("Interval").setHeader("Interval");
        grid.addColumn(MonthlyExpensesProjection::getAmount).setKey("Amount").setHeader("Amount");
        grid.addColumn(MonthlyExpensesProjection::getDaysPassed).setKey("Days Passed").setHeader("Days Passed");
        grid.addColumn(MonthlyExpensesProjection::getTimesTriggered).setKey("Times Triggered").setHeader("Times Triggered");
        grid.addColumn(MonthlyExpensesProjection::getStartDate).setKey("Start Date").setHeader("Start Date");
        grid.addColumn(MonthlyExpensesProjection::getEndDate).setKey("End Date").setHeader("End Date");

        grid.getColumns().forEach(c -> {
            c.setSortable(true);
            c.setAutoWidth(true);
        });
        grid.setColumnReorderingAllowed(true);

        grid.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'hidden';");

        chartPie.getElement().addEventListener("click", event -> filterByChartSelection());
    }

    private void filterByChartSelection() {
        chartPie.getElement()
                .executeJs("return this.getAttribute('data-selected');")
                .then(String.class, selected -> {
                    selected = Objects.requireNonNullElse(selected, "");
                    selectedCategoryName.set(selected);
                    applyFilter();
                });

        chartPie.getElement()
                .executeJs("return this.getAttribute('legend-hidden');")
                .then(String.class, legendHiddenItems -> {
                    legendHiddenCategories.set(getLegendHiddenCategories(legendHiddenItems));
                    applyFilter();
                });
    }

    private void applyFilter() {
        dataView.setFilter(projection -> {
            String categoryName = projection.getCategoryName();
            boolean filterByNonHiddenCategories = !legendHiddenCategories.get().contains(categoryName);
            boolean filterBySelectedCategory = selectedCategoryName.get().equalsIgnoreCase(categoryName);

            if (selectedCategoryName.get().isEmpty() && legendHiddenCategories.get().isEmpty()) {
                return true; // No filter is applied, display all
            }

            if (legendHiddenCategories.get().isEmpty()) {
                return filterBySelectedCategory;
            }

            if (selectedCategoryName.get().isEmpty()) {
                return filterByNonHiddenCategories;
            }

            return filterBySelectedCategory && filterByNonHiddenCategories;
        });
    }


    private List<String> getLegendHiddenCategories(String legendHiddenStr) {
        if (legendHiddenStr == null || legendHiddenStr.isEmpty()) {
            return List.of();
        }

        return Arrays.asList(legendHiddenStr.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(","));
    }

    private void initializeChart() {
        Map<String, Double> totalMonthlyExpensesGroupedByCategory = expenseService.getMonthlyExpensesByUser(LocalDate.now())
                .stream()
                .collect(Collectors.groupingBy(
                        MonthlyExpensesProjection::getCategoryName,
                        Collectors.summingDouble(p -> p.getAmount() * p.getTimesTriggered())
                ));

        AtomicInteger index = new AtomicInteger(0);
        JsonArray jsonOptionData = Json.createArray();

        totalMonthlyExpensesGroupedByCategory.forEach((categoryName, totalSum) -> {
            JsonObject jsonObject = Json.createObject();
            jsonObject.put("name", categoryName);
            jsonObject.put("value", totalSum);
            jsonOptionData.set(index.get(), jsonObject);

            index.addAndGet(1);
        });

        UI.getCurrent().getPage().executeJs("fillChartPie($0);", jsonOptionData.toJson());
    }


}
