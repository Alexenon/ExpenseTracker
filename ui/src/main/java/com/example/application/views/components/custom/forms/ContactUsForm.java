package com.example.application.views.components.custom.forms;

import com.example.application.views.components.core.Form;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Tag("form")
public class ContactUsForm extends Form {

	@Getter
	private final TextField name = new TextField("Name");

	@Getter
	private final EmailField email = new EmailField("Email");

	@Getter
	private final TextArea message = new TextArea("Message");

	@Getter
	private final Button sendBtn = new Button("Send");

	public ContactUsForm() {
		add(name, email, message, sendBtn);
	}

}
