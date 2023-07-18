package com.example.application.views.main;

import com.example.application.dtos.ExpenseDTO;
import com.example.application.services.CategoryService;
import com.example.application.services.ExpenseService;
import com.example.application.services.TimestampService;
import com.example.application.views.main.components.AddExpenseDialog;
import com.example.application.views.main.components.EditExpenseDialog;
import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Expenses")
@Route(value = "expenses", layout = MainLayout.class)
public class ExpensesView extends Main {

    private static final Logger logger = LoggerFactory.getLogger(ExpensesView.class);

    private final ExpenseService expenseService;
    private final TimestampService timestampService;
    private final CategoryService categoryService;
    private final DatePicker.DatePickerI18n singleFormatI18n;

    private final TextField filterText = new TextField();
    private final Grid<ExpenseDTO> grid = new Grid<>(ExpenseDTO.class);

    @Autowired
    public ExpensesView(ExpenseService expenseService,
                        TimestampService timestampService,
                        CategoryService categoryService,
                        DatePicker.DatePickerI18n singleFormatI18n) {
        this.expenseService = expenseService;
        this.timestampService = timestampService;
        this.categoryService = categoryService;
        this.singleFormatI18n = singleFormatI18n;

        addClassName("page-content");
        add(
                getToolBar(),
                getGrid()
        );
        logger.info("Expenses Page accessed");
    }

    private Component getToolBar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        Button addBtn = new Button("Add");
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(event -> {
            logger.info("Clicked on Add button");
            AddExpenseDialog dialog = new AddExpenseDialog(expenseService, timestampService, categoryService, singleFormatI18n);
            dialog.open();
            dialog.addClickSaveBtnListener(grid -> updateGrid());
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
                    button.setIcon(new Icon(VaadinIcon.EDIT));
                    button.addClickListener(e -> {
                        logger.info("Clicked on Edit button for expense {}", expense.getName());
                        EditExpenseDialog dialog = new EditExpenseDialog(
                                expense,
                                expenseService,
                                timestampService,
                                categoryService,
                                singleFormatI18n
                        );
                        dialog.open();
                        dialog.addClickSaveBtnListener(grid -> updateGrid());
                    });
                })
        ).setHeader("Edit");

        // Delete Column
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, expense) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> {
                        logger.info("Clicked on Delete button for expense {}", expense.getName());
                        ConfirmDialog dialog = getConfirmationDialog(expense.getName());
                        dialog.open();
                        dialog.addConfirmListener(l -> {
                            logger.info("Deleted expense {}", expense.getName());
                            expenseService.deleteExpenseById(expense.getId());
                            updateGrid();
                        });
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })
        ).setHeader("Delete");

        grid.getColumns().forEach(c -> c.setAutoWidth(true));
        updateGrid();

        return grid;
    }

    private void updateGrid() {
        logger.info("Updated Expense Table");
        grid.setItems(expenseService.getAllExpenses());
    }

    private ConfirmDialog getConfirmationDialog(String text) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete this expense '" + text + "`");
        dialog.setText("Are you sure you want to permanently delete this item?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        return dialog;
    }

}




