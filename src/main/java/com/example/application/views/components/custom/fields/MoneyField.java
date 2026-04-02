package com.example.application.views.components.custom.fields;

import com.example.application.utils.common.lang.StringUtils;
import com.example.application.views.components.utils.convertors.FlexiblePriceConvertor;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;

import java.math.BigDecimal;

public class MoneyField extends AbstractNumberTextField {

	private final Component prefixComponent = new Paragraph("$");

	public MoneyField() {
		this(null);
	}

	public MoneyField(String label) {
		this(label, true);
	}

	public MoneyField(String label, boolean formatable) {
		super(label, formatable);
		numberFormat.setMinimumFractionDigits(2);

		setAllowedCharPattern("[0-9.]");
		setPrefixComponent(prefixComponent);
		setFormatable(formatable);
	}

	public void setPrefix(boolean isPrefix) {
		if (isPrefix && !prefixComponent.isAttached())
			setPrefixComponent(prefixComponent);
		else {
			prefixComponent.removeFromParent();
		}
	}

	public void setValue(BigDecimal value) {
		String parsedValue = parse(value);
		String formatedValue = StringUtils.stripTrailingZeroes(parsedValue);
		super.setValue(formatedValue);
	}

	public BigDecimal getMoneyAmount() {
		String rawValue = getValue();

		if(rawValue == null || rawValue.isEmpty())
			return BigDecimal.ZERO;

		String parsedAmount = rawValue.replaceAll(",", "");
		return new BigDecimal(parsedAmount);
	}

	private String parse(BigDecimal value) {
		if (value.compareTo(BigDecimal.ZERO) == 0)
			return "0";

		numberFormat.setMaximumFractionDigits(FlexiblePriceConvertor.maxDecimalPlaces(value));

		return numberFormat.format(value);
	}


}
