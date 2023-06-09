package com.example.application.views.main;

import com.example.application.model.ExpenseDTO;
import com.example.application.service.CategoryService;
import com.example.application.service.ExpenseService;
import com.example.application.service.TimestampService;
import com.example.application.views.main.components.AddExpenseDialog;
import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Expenses")
@Route(value = "expenses", layout = MainLayout.class)
public class ExpensesView extends VerticalLayout {

    private final ExpenseService expenseService;
    private final TimestampService timestampService;
    private final CategoryService categoryService;

    private final TextField filterText = new TextField();
    private final Grid<ExpenseDTO> grid = new Grid<>(ExpenseDTO.class);

    public ExpensesView(ExpenseService expenseService, TimestampService timestampService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.timestampService = timestampService;
        this.categoryService = categoryService;

        this.setClassName("page-content");
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
        addBtn.addClickListener(event -> {
            AddExpenseDialog dialog = new AddExpenseDialog(expenseService, timestampService, categoryService);
            dialog.open();
        });

        final HorizontalLayout toolBar = new HorizontalLayout(filterText, addBtn);
        toolBar.addClassName("toolbar");

        return toolBar;
    }

    private Component getGrid() {
        grid.addClassName("expenses-grid");
        grid.setColumns("name", "category", "amount", "timestamp");

        // Edit Column
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, expense) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> System.out.println("Clicking on edit"));
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                })).setHeader("Edit");

        // Delete Column
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, expense) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        ConfirmDialog dialog = getConfirmationDialog(expense.getName());
                        dialog.open();
                        dialog.addConfirmListener(l -> expenseService.deleteExpenseById(expense.getId()));
                        updateGrid();
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Delete");

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        updateGrid();
        return grid;
    }

    private void updateGrid() {
        grid.setItems(expenseService.getAllExpenses());
    }

    private ConfirmDialog getConfirmationDialog(String text) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Deleting \"" + text + "\"?");
        dialog.setText("Are you sure you want to permanently delete this item?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        return dialog;
    }

}




