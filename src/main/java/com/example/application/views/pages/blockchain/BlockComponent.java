package com.example.application.views.pages.blockchain;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.ThemeList;

import java.util.List;
import java.util.Optional;

/*
	TODO:
		- Add loading icon animation inside mineButton or near it
* */

public class BlockComponent extends VerticalLayout {

	private static final String SOFT_BLUE_COLOR = "#d6ebff";
	private static final String SOFT_RED_COLOR = "#ffd6d6";
	private static final String SOFT_GREEN_COLOR = "#e1f5e1";

	private final BlockNode node;

	private final IntegerField nonceField = new IntegerField("Nonce");
	private final TextArea dataField = new TextArea("Data");
	private final TextField prevField = new TextField("Prev");
	private final TextField hashField = new TextField("Hash");
	private final Button mineButton = new Button("Mine");

	private Span statusBadge = new Span();

	public BlockComponent(String name, BlockNode node) {
		this.node = node;
		setWidth("400px");
		setPadding(true);
		setSpacing(true);

		addClassName("block-card");
		nonceField.addClassName("block-input");
		dataField.addClassName("block-textarea");
		prevField.addClassName("block-input");
		hashField.addClassName("block-input");
		mineButton.addClassName("block-button");

		initializeFields();
		updateNodeValues();
		applyColor();
		initializeFieldsListeners(node);

		add(
				new H3(name),
				statusBadge,
				nonceField,
				dataField,
				prevField,
				hashField,
				mineButton
		);
	}

	private void initializeFields() {
		nonceField.setWidth("200px");
		nonceField.setStepButtonsVisible(true);
		nonceField.setMin(0);
		nonceField.setMax(Integer.MAX_VALUE);
		nonceField.setHelperText("A value adjusted during mining so the block’s hash starts with ‘0000’. The correct nonce proves the block is valid");
		nonceField.setValueChangeMode(ValueChangeMode.EAGER);

		dataField.setWidthFull();
		dataField.setHeight("120px");
		dataField.setHelperText("Data that is stored about this block and it's converted into hash");
		dataField.setValueChangeMode(ValueChangeMode.EAGER);

		prevField.setWidthFull();
		prevField.setValueChangeMode(ValueChangeMode.EAGER);
		prevField.setHelperText("This is the hash value for the previous block");
		prevField.setReadOnly(true);

		hashField.setWidthFull();
		hashField.setValueChangeMode(ValueChangeMode.EAGER);
		hashField.setHelperText("This is the hash value for current block, generated from it's data, nonce and previous hash block");
		hashField.setReadOnly(true);
	}

	private void initializeFieldsListeners(BlockNode node) {
		nonceField.addValueChangeListener(field -> {
			node.setNonce(field.getValue());
			updateHashFieldValue();
			applyColor();
		});

		dataField.addValueChangeListener(field -> {
			node.setData(field.getValue());
			updateHashFieldValue();
			applyColor();
		});

		mineButton.addClickListener(field -> {
			node.mineBlock();
			updateNodeValues();
			applyColor();
		});

		hashField.addValueChangeListener(field -> {
			updateHashFieldValue();
			applyColor();
		});
	}

	public void updateNodeValues() {
		String prevHash = Optional.ofNullable(node.getPreviousNode())
				.map(BlockNode::getHash)
				.orElse("");

		nonceField.setValue(node.getNonce() == null ? 0 : node.getNonce());
		dataField.setValue(node.getData() == null ? "" : node.getData());
		prevField.setValue(prevHash);
		hashField.setValue(node.getHash() == null ? "" : node.getHash());
	}

	private void updateHashFieldValue() {
		String hash = Optional.ofNullable(node)
				.map(BlockNode::getHash)
				.orElse("");

		hashField.setValue(hash);
		notifyHashChanged();
	}

	private void notifyHashChanged() {
		UI.getCurrent().access(() -> ComponentUtil.fireEvent(UI.getCurrent(), new BlockNodeCreatedOrUpdatedEvent(this, node)));
	}

	public void applyColor() {
		if (!node.isValid()) {
			getStyle().set("background-color", SOFT_RED_COLOR);
			statusBadge = createStatusBadge("Invalid", VaadinIcon.EXCLAMATION_CIRCLE_O.create(), "error");
			return;
		}

		if (node.isMined()) {
			getStyle().set("background-color", SOFT_BLUE_COLOR);
			statusBadge = createStatusBadge("Mined", VaadinIcon.CHECK.create(), "success");
			return;
		}

		getStyle().set("background-color", SOFT_GREEN_COLOR);
		statusBadge = createStatusBadge("Waiting", VaadinIcon.HAND.create(), "contrast");
	}

	private Span createStatusBadge(String name, Icon icon, String theme) {
		icon.getStyle().set("padding", "var(--lumo-space-xs)");
		ThemeList themeList = statusBadge.getElement().getThemeList();
		List.of("success", "contrast", "error").forEach(themeList::remove);
		themeList.add("badge");
		themeList.add(theme);
		return new Span(icon, new Span(name));
	}

}
