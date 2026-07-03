package com.example.application.views.components.custom.fields;

import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class AbstractNumberTextField extends TextField {

	protected NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
	protected NumeralFieldFormatter formatter;

	public AbstractNumberTextField(String label, boolean formatable) {
		super(label);
		this.numberFormat.setGroupingUsed(true);
		this.numberFormat.setParseIntegerOnly(false);
		this.numberFormat.setMaximumIntegerDigits(12);

		this.formatter = new NumeralFieldFormatter.Builder()
				.delimiter(",")
				.decimalMark(".")
				.decimalScale(6)
				.nonNegativeOnly(true)
				.stripLeadingZeroes(true)
				.build();

		setAllowedCharPattern("[0-9.]");
		setFormatable(formatable);
	}

	public void setFormatter(NumeralFieldFormatter formatter) {
		this.formatter = formatter;
		setFormatable(formatter != null);
	}

	public void setFormatable(boolean formatable) {
		if (formatable) {
			formatter.extend(this);
		} else {
			formatter.remove();
		}
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
	}

}
