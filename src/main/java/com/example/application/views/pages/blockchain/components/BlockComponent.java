package com.example.application.views.pages.blockchain.components;

import com.example.application.data.models.blockchain.BlockNode;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.FieldWithTooltip;
import com.example.application.views.components.custom.fields.helpers.InfoTooltip;
import com.example.application.views.pages.blockchain.events.BlockNodeUpdatedEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
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
import java.util.Objects;
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

	private final IntegerField nonceField = new IntegerField();
	private final TextArea dataField = new TextArea();
	private final TextField prevField = new TextField();
	private final TextField hashField = new TextField();
	private final Button mineButton = new Button("Mine");
	private final Span statusBadge = new Span();

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
		applyBadgeAndColor();
		initializeFieldsListeners(node);

		add(
				createHeader(name),
				createBody(
						addField("Nonce", nonceField, "A value adjusted during mining so the block’s hash starts with ‘0000’. The correct nonce proves the block is valid"),
						addField("Data", dataField, "Data that is stored about this block and it's converted into hash"),
						addField("Previous hash", prevField, "This is the hash value for the previous block"),
						addField("Current hash", hashField, "This is the hash value for current block, generated from it's data, nonce and previous hash block")
				),
				mineButton
		);
	}

	public BlockNode getNode() {
		return node;
	}

	private void initializeFields() {
		nonceField.setStepButtonsVisible(true);
		nonceField.setMin(0);
		nonceField.setMax(Integer.MAX_VALUE);
		nonceField.setValueChangeMode(ValueChangeMode.EAGER);

		dataField.setWidthFull();
		dataField.setHeight("120px");
		dataField.setValueChangeMode(ValueChangeMode.EAGER);

		prevField.setWidthFull();
		prevField.setValueChangeMode(ValueChangeMode.EAGER);
		prevField.setReadOnly(true);

		hashField.setWidthFull();
		hashField.setValueChangeMode(ValueChangeMode.EAGER);
		hashField.setReadOnly(true);
	}

	private void initializeFieldsListeners(BlockNode node) {
		nonceField.addValueChangeListener(field -> {
			node.setNonce(field.getValue());
			updateHashFieldValue();
			applyBadgeAndColor();
		});

		dataField.addValueChangeListener(field -> {
			node.setData(field.getValue());
			updateHashFieldValue();
			applyBadgeAndColor();
		});

		mineButton.addClickListener(field -> {
			node.mineBlock();
			updateNodeValues();
			updateHashFieldValue();
			applyBadgeAndColor();
		});

		hashField.addValueChangeListener(field -> {
			updateHashFieldValue();
			applyBadgeAndColor();
		});
	}

	private void updateNodeValues() {
		String prevHash = Optional.ofNullable(node.getPreviousNode())
				.map(BlockNode::getHash)
				.orElse("");

		nonceField.setValue(Objects.requireNonNullElse(node.getNonce(), 0));
		dataField.setValue(Objects.requireNonNullElse(node.getData(), ""));
		prevField.setValue(prevHash);
		hashField.setValue(Objects.requireNonNullElse(node.getHash(), ""));
	}

	private void updateHashFieldValue() {
		String hash = Optional.ofNullable(node)
				.map(BlockNode::getHash)
				.orElse("");

		hashField.setValue(hash);
		notifyHashChanged();
	}

	private void notifyHashChanged() {
		UI.getCurrent().access(() -> ComponentUtil.fireEvent(UI.getCurrent(), new BlockNodeUpdatedEvent(this, node)));
	}

	public void applyBadgeAndColor() {
		switch (node.getStatus()) {
			case INVALID -> {
				getStyle().set("background-color", SOFT_RED_COLOR);
				updateStatusBadge("Invalid", VaadinIcon.EXCLAMATION_CIRCLE_O.create(), "error");
			}
			case MINED -> {
				getStyle().set("background-color", SOFT_BLUE_COLOR);
				updateStatusBadge("Mined", VaadinIcon.CHECK.create(), "success");
			}
			case WAITING -> {
				getStyle().set("background-color", SOFT_GREEN_COLOR);
				updateStatusBadge("Waiting", VaadinIcon.HAND.create(), "contrast");
			}
		}
	}

	private Span createStatusBadge(String name, Icon icon, String theme) {
		icon.getStyle().set("padding", "var(--lumo-space-xs)");
		ThemeList themeList = statusBadge.getElement().getThemeList();
		List.of("success", "contrast", "error").forEach(themeList::remove);
		themeList.add("badge");
		themeList.add(theme);
		return new Span(icon, new Span(name));
	}

	private void updateStatusBadge(String text, Icon icon, String theme) {
		statusBadge.removeAll();

		icon.getStyle().set("padding", "var(--lumo-space-xs)");

		ThemeList themes = statusBadge.getElement().getThemeList();
		List.of("success", "contrast", "error").forEach(themes::remove);

		themes.add("badge");
		themes.add(theme);

		statusBadge.add(icon, new Span(text));
	}

	private FieldWithTooltip addField(String label, Component component, String tooltipText) {
		return new FieldWithTooltip(label, component, tooltipText);
	}

	// Extract this to separate component if used very often
	private Div addFieldWithTooltip(Component component, String tooltipText) {
		return Container.builder("tooltip-container")
				.addComponent(component)
				.addComponent(new InfoTooltip(tooltipText).getIcon())
				.build();
	}

	private Div createHeader(String name) {
		return Container.builder("card-header")
				.addComponent(new H3(name))
				.addComponent(statusBadge)
				.build();
	}

	private Div createBody(Component... components) {
		return Container.builder("card-body")
				.addComponents(components)
				.build();
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BlockComponent candidate)) return false;

		return Objects.equals(node, candidate.node);
	}

}