package com.example.application.views.pages.settings.tabs;

import com.example.application.data.dtos.expense.CategoryDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.expenses.AddCategoryDialog;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.CommonButtons;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class CategoriesTab extends SettingsAbstractTab {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final Grid<CategoryDTO> grid = new Grid<>();
	private final Button addCategoryBtn = CommonButtons.createAddButton("Add category");

	public CategoriesTab(InstrumentsFacadeService instrumentsFacadeService) {
		super("Categories", PictogramIcon.TABLET_CELLPHONE.create());
		this.instrumentsFacadeService = instrumentsFacadeService;
		initializeContent();
		initializeGrid();
	}

	@Override
	public Div getContent() {
		return Container.builder()
				.addComponent(addCategoryBtn)
				.addComponent(grid)
				.build();
	}

	private void initializeContent() {
		addCategoryBtn.addClickListener(e -> {
			new AddCategoryDialog(instrumentsFacadeService).open();
		});
	}

	private void initializeGrid() {
		grid.setItems(instrumentsFacadeService.findUserCategories());
		grid.addColumn(CategoryDTO::getName).setKey("Name").setHeader("Category name");
		grid.addColumn(CategoryDTO::getIconName).setKey("Icon").setHeader("Category icon");

		grid.getColumns().forEach(c -> {
			c.setSortable(true);
			c.setAutoWidth(true);
		});
		grid.setColumnReorderingAllowed(false);

		grid.getElement().executeJs("this.shadowRoot.querySelector('table').style.overflow = 'hidden';");
	}

	private ComponentRenderer<Button, CategoryDTO> columnEditRenderer() {
		return new ComponentRenderer<>(Button::new, (button, category) -> {
			button.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
//				EditExpenseDialog dialog = new EditExpenseDialog(
//						category,
//						instrumentsFacadeService,
//						singleFormatI18n
//				);
//				dialog.open();
//				dialog.addSaveBtnClickListener(grid -> updateGrid());
			});
		});
	}

	private ComponentRenderer<Button, CategoryDTO> columnDeleteRenderer() {
		return new ComponentRenderer<>(Button::new, (button, category) -> {
			button.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				ConfirmDialog dialog = getConfirmationDialog(category.getName());
				dialog.open();
				dialog.addConfirmListener(l -> {
					instrumentsFacadeService.deleteCategory(category.getId());
					updateGrid();
				});
			});
		});
	}

	private ConfirmDialog getConfirmationDialog(String text) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Delete this category '" + text + "`");
		dialog.setText("Are you sure you want to permanently delete this item?");

		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.setConfirmButtonTheme("error primary");

		return dialog;
	}

	private void updateGrid() {
		grid.setItems(instrumentsFacadeService.findUserCategories());
	}

}
