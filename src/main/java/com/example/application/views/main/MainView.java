package com.example.application.views.main;

import com.example.application.model.Expense;
import com.example.application.service.ExpenseService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Main")
@Route(value = "")
public class MainView extends HorizontalLayout {

    TextField filterText = new TextField();
    Grid<Expense> grid = new Grid<>(Expense.class);

    @Autowired
    private ExpenseService expenseService;

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
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        final HorizontalLayout toolBar = new HorizontalLayout(filterText, addBtn);
        toolBar.addClassName("toolbar");

        return toolBar;
    }

    private Component getGrid() {
        grid.addClassName("expenses-grid");
        grid.setSizeFull();
        grid.setColumns("name", "amount", "description", "date", "timestamp");
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        grid.addColumn(person -> createButton("Add", VaadinIcon.PLUS))
                .setHeader("Add")
                .setFlexGrow(0)
                .setWidth("100px")
                .setResizable(false);

        grid.addColumn(person -> createButton("Edit", VaadinIcon.EDIT))
                .setHeader("Edit")
                .setFlexGrow(0)
                .setWidth("100px")
                .setResizable(false);

        grid.addColumn(person -> createButton("Remove", VaadinIcon.TRASH))
                .setHeader("Remove")
                .setFlexGrow(0)
                .setWidth("100px")
                .setResizable(false);

        updateGrid();

        return grid;
    }

    private Button createButton(String text, VaadinIcon icon) {
        return new Button(text, icon.create());
    }

    private void updateGrid() {
        if (expenseService == null) {
            System.out.println("ExpenseService is null");
        } else {
            System.out.println("ExpenseService is not null");
            List<Expense> allExpenses = expenseService.getAllExpenses();
            System.out.println("Number of expenses: " + allExpenses.size());
            grid.setItems(allExpenses);
        }
    }


}
