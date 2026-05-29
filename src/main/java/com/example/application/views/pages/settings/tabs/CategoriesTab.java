package com.example.application.views.pages.settings.tabs;

import com.example.application.entities.expenses.Category;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.CommonButtons;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;

public class CategoriesTab extends SettingsAbstractTab {

	private final Grid<Category> categoryGrid = new Grid<>();
	private final Button addCategoryBtn = CommonButtons.createAddButton("Add category");

	public CategoriesTab() {
		super("Categories", PictogramIcon.TABLET_CELLPHONE.create());
	}

	private void initializeContent() {
		addCategoryBtn.addClickListener(e -> {
		});
	}


}
