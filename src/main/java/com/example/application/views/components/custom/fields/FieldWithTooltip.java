package com.example.application.views.components.custom.fields;

import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.fields.helpers.InfoTooltip;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

public class FieldWithTooltip extends Div {

	private final Paragraph label;
	private final Component field;
	private final Container header;

	public FieldWithTooltip(String labelText, Component field, String tooltip) {
		this(labelText, field);
		setTooltip(tooltip);
	}

	public FieldWithTooltip(String labelText, Component field) {
		this.field = field;
		this.label = new Paragraph(labelText);
		this.header = Container.builder("field-header")
				.addComponent(label)
				.build();
		initialize();
	}

	private void initialize() {
		addClassName("field-container");
		label.addClassName("field-label");

		add(
				header,
				field
		);
	}

	public void setTooltip(String text) {
		InfoTooltip tooltip = new InfoTooltip(text);
		header.add(tooltip.getIcon());
	}

}
