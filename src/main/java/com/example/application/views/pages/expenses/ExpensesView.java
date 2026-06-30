package com.example.application.views.pages.expenses;

import com.example.application.data.dtos.expense.ExpenseDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.custom.dialogs.AddExpenseDialog;
import com.example.application.views.components.custom.dialogs.EditExpenseDialog;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
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
public class ExpensesView extends DefaultPage {

	private static final Logger logger = LoggerFactory.getLogger(ExpensesView.class);

	private final InstrumentsFacadeService instrumentsFacadeService;
	private final DatePicker.DatePickerI18n singleFormatI18n;

	private final TextField filterText = new TextField();
	private final Grid<ExpenseDTO> grid = new Grid<>(ExpenseDTO.class);

	@Autowired
	public ExpensesView(
			InstrumentsFacadeService instrumentsFacadeService,
			DatePicker.DatePickerI18n singleFormatI18n)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
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
			AddExpenseDialog dialog = new AddExpenseDialog(instrumentsFacadeService, singleFormatI18n);
			dialog.open();
			dialog.addSaveBtnClickListener(grid -> updateGrid());
		});

		final HorizontalLayout toolBar = new HorizontalLayout(filterText, addBtn);
		toolBar.addClassName("toolbar");

		return toolBar;
	}

	private Component getGrid() {
		grid.addClassName("expenses-grid");
		grid.setColumns("name", "category", "amount", "timestamp");

		grid.addColumn(columnEditRenderer()).setHeader("Edit");
		grid.addColumn(columnDeleteRenderer()).setHeader("Delete");

		grid.getColumns().forEach(c -> c.setAutoWidth(true));
		updateGrid();

		return grid;
	}

	private ComponentRenderer<Button, ExpenseDTO> columnEditRenderer() {
		return new ComponentRenderer<>(Button::new, (button, expense) -> {
			button.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				logger.info("Clicked on editButton for expense: #{}", expense.getId());
				EditExpenseDialog dialog = new EditExpenseDialog(
						expense,
						instrumentsFacadeService,
						singleFormatI18n
				);
				dialog.open();
				dialog.addSaveBtnClickListener(grid -> updateGrid());
			});
		});
	}

	private ComponentRenderer<Button, ExpenseDTO> columnDeleteRenderer() {
		return new ComponentRenderer<>(Button::new, (button, expense) -> {
			button.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				logger.info("Clicked on Delete button for expense {}", expense.getName());
				ConfirmDialog dialog = getConfirmationDialog(expense.getName());
				dialog.open();
				dialog.addConfirmListener(l -> {
					logger.info("Deleted expense {}", expense.getName());
					instrumentsFacadeService.deleteExpense(expense.getId());
					updateGrid();
				});
			});
		});
	}

	private void updateGrid() {
		logger.info("Updated Expense Table");
		grid.setItems(instrumentsFacadeService.findAllUserExpenses());
	}

	private ConfirmDialog getConfirmationDialog(String expenseName) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Delete this expense '" + expenseName + "`");
		dialog.setText("Are you sure you want to permanently delete this item?");

		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.setConfirmButtonTheme("error primary");

		return dialog;
	}

}




