package com.example.application.views.components.core;

import com.vaadin.flow.component.html.Paragraph;

public class ErrorField extends Paragraph {

	public ErrorField() {
		this(true);
	}

	public ErrorField(String className) {
		this(true);
		addClassName(className);
	}

	public ErrorField(boolean isVisible) {
		super();
		this.getStyle().set("color", "red");
	}

}
