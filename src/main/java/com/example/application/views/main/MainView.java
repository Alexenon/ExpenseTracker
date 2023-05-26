package com.example.application.views.main;

import com.example.application.data.Expense;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Main")
@Route(value = "")
public class MainView extends HorizontalLayout {

    Grid<Expense> grid = new Grid<>(Expense.class);

    public MainView() {
        configureGrid();

        add(getToolBar());
        add(grid);
    }

    private Component getToolBar() {
        TextField filterText = new TextField();
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button addBtn = new Button("Add");
        final HorizontalLayout toolBar = new HorizontalLayout(filterText, addBtn);
        toolBar.addClassName("toolbar");

        return toolBar;
    }

    private void configureGrid() {
        grid.addClassName("expenses-grid");
        grid.setSizeFull();
        grid.setColumns("name", "amount", "description", "expenseInterval", "date");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
    }

}
