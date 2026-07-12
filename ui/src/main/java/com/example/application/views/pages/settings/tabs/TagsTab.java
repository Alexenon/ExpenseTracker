package com.example.application.views.pages.settings.tabs;

import com.example.application.InstrumentsFacadeService;
import com.example.application.tag.TagDTO;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.expenses.AddTagDialog;
import com.example.application.views.components.custom.dialogs.expenses.EditTagDialog;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.example.application.views.components.utils.CommonButtons;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class TagsTab extends AbstractSettingsTab implements HasNotifications {

	private final InstrumentsFacadeService instrumentsFacadeService;

	private final Grid<TagDTO> grid = new Grid<>(TagDTO.class, false);
	private final Button addTagBtn = CommonButtons.createAddButton("Add tag");

	public TagsTab(InstrumentsFacadeService instrumentsFacadeService) {
		super("Tags", PictogramIcon.TABLET_CELLPHONE.create());
		this.instrumentsFacadeService = instrumentsFacadeService;
		initializeContent();
		initializeGrid();
	}

	@Override
	public Div getContent() {
		return Container.builder()
				.addComponent(addTagBtn)
				.addComponent(grid)
				.build();
	}

	private void initializeContent() {
		addTagBtn.addClickListener(e -> new AddTagDialog(instrumentsFacadeService).open());
	}

	private void initializeGrid() {
		grid.setItems(instrumentsFacadeService.findUserTags());
		grid.addColumn(TagDTO::getName).setKey("Name").setHeader("Tag name").setSortable(true);
		grid.addColumn(columnEditRenderer()).setHeader("Edit");
		grid.addColumn(columnDeleteRenderer()).setHeader("Delete");

		grid.setId("tags-grid");
		grid.getColumns().forEach(c -> c.setAutoWidth(true));
	}

	private ComponentRenderer<Button, TagDTO> columnEditRenderer() {
		return new ComponentRenderer<>(Button::new, (button, tag) -> {
			button.setIcon(PictogramIcon.SQUARE_EDIT_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(e -> {
				EditTagDialog dialog = new EditTagDialog(tag, instrumentsFacadeService);
				dialog.open();
			});
		});
	}

	private ComponentRenderer<Button, TagDTO> columnDeleteRenderer() {
		return new ComponentRenderer<>(Button::new, (button, tag) -> {
			button.setIcon(PictogramIcon.DELETE_OUTLINE.create("grid-action-btn"));
			button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
			button.addClickListener(btn -> {
				ConfirmDialog dialog = getConfirmationDialog(tag.getName());
				dialog.open();
				dialog.addConfirmListener(l -> {
					instrumentsFacadeService.deleteTag(tag.getId());
					updateGrid();
				});
			});
		});
	}

	private ConfirmDialog getConfirmationDialog(String text) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("Delete this tag '" + text + "`");
		dialog.setText("Are you sure you want to permanently delete this item?");

		dialog.setCancelable(true);
		dialog.setConfirmText("Delete");
		dialog.setConfirmButtonTheme("error primary");

		return dialog;
	}

	private void updateGrid() {
		grid.setItems(instrumentsFacadeService.findUserTags());
	}

}
