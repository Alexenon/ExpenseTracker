package com.example.application.views.main;

import com.example.application.entities.Expense;
import com.example.application.services.ExpenseService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@PageTitle("Main")
@Route(value = "")
public class MainView extends HorizontalLayout {

    TextField filterText = new TextField();
    Grid<Expense> grid = new Grid<>(Expense.class);

    @Autowired
    private ExpenseService expService;

    public MainView() {
        add(
                getToolBar(),
                getGrid()
        );
    }

    private Component getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        Button addBtn = new Button("Add");
        final HorizontalLayout toolBar = new HorizontalLayout(filterText, addBtn);
        toolBar.addClassName("toolbar");

        return toolBar;
    }

    private Component getGrid() {
        grid.addClassName("expenses-grid");
        grid.setSizeFull();
        grid.setColumns("name", "amount", "description", "expenseInterval", "date");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        return grid;
    }

    // TODO: Update it to use filter text
    private void updateGrid() {
        grid.setItems(expService.getAllExpenses());
//        grid.setItems(expService.searchExpenses(filterText.getValue()));
    }

}
