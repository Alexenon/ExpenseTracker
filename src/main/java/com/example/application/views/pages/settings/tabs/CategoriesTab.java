package com.example.application.views.pages.settings.tabs;

import com.example.application.data.dtos.expense.CategoryDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.exceptions.DeleteCategoryWithExpensesException;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.expenses.AddCategoryDialog;
import com.example.application.views.components.custom.dialogs.expenses.EditCategoryDialog;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.CommonButtons;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class CategoriesTab extends SettingsAbstractTab implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final Grid<CategoryDTO> grid = new Grid<>(CategoryDTO.class, false);
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
		addCategoryBtn.addClickListener(e -> new AddCategoryDialog(instrumentsFacadeService).open());
	}

	private void initializeGrid() {
		grid.setItems(instrumentsFacadeService.findUserCategories());
		grid.addColumn(CategoryDTO::getName).setKey("Name").setHeader("Category name").setSortable(true);
		grid.addColumn(columnCategoryIconRenderer()).setKey("Icon").setHeader("Category icon");
		grid.addColumn(columnEditRenderer()).setHeader("Edit");
		grid.addColumn(columnDeleteRenderer()).setHeader("Delete");

		grid.setId("categories-grid");
		grid.getColumns().forEach(c -> c.setAutoWidth(true));
	}

	private ComponentRenderer<Div, CategoryDTO> columnCategoryIconRenderer() {
		return new ComponentRenderer<>(category -> {
			Div div = new Div();
			div.addClassName("centered-container");

			PictogramIcon.findByName(category.getIconName())
					.ifPresent(icon -> {
						MonoIcon monoIcon = icon.create();
						monoIcon.getStyle().set("cursor", "default");
						div.add(monoIcon);
					});

			return div;
		});
	}

	private ComponentRenderer<Button, CategoryDTO> columnEditRenderer() {
		return new ComponentRenderer<>(Button::new, (button, category) -> {
			button.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				EditCategoryDialog dialog = new EditCategoryDialog(category, instrumentsFacadeService);
				dialog.open();
//				dialog.addSaveBtnClickListener(grid -> updateGrid());
			});
		});
	}

	private ComponentRenderer<Button, CategoryDTO> columnDeleteRenderer() {
		return new ComponentRenderer<>(Button::new, (button, category) -> {
			button.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(btn -> {
				ConfirmDialog dialog = getConfirmationDialog(category.getName());
				dialog.open();
				dialog.addConfirmListener(l -> {
					try {
						instrumentsFacadeService.deleteCategory(category.getId());
					} catch (DeleteCategoryWithExpensesException e) {
						showErrorNotification(e.getLocalizedMessage());
					}
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
