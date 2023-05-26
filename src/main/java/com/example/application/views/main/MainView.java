package com.example.application.views.main;

import com.example.application.data.Expense;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Main")
@Route(value = "")
public class MainView extends HorizontalLayout {

    Grid<Expense> grid = new Grid<Expense>();
    TextField filterText = new TextField();

    public MainView() {
        configureGrid();
        add(getToolBar());
    }

    private Component getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        return null;
    }

    private void configureGrid() {
        grid.addClassName("expenses-grid");
        grid.setSizeFull();
        grid.setColumns("Id", "Expense", "Amount", "Description", "Interval", "Date created");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));
    }

}
