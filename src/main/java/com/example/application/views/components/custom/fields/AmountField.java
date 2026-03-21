package com.example.application.views.components.custom.fields;

import com.example.application.utils.common.lang.StringUtils;

import java.math.BigDecimal;

public class AmountField extends AbstractNumberTextField {

	public AmountField() {
		this(null);
	}

	public AmountField(String label) {
		this(label, true);
	}

	public AmountField(String label, boolean formatable) {
		super(label, formatable);
		setAllowedCharPattern("[0-9.]");
		numberFormat.setMinimumFractionDigits(0);
	}

	public void setValue(BigDecimal value) {
		String formatedValue = StringUtils.stripTrailingZeroes(parse(value));
		super.setValue(formatedValue);
	}

	public BigDecimal getAmount() {
		try {
			String value = this.getValue();

			if (value == null || value.isEmpty() || value.isBlank())
				return BigDecimal.ZERO;

			return new BigDecimal(value);
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	private String parse(BigDecimal value) {
		if (value.compareTo(new BigDecimal("1000")) >= 0) {
			numberFormat.setMaximumFractionDigits(2);
		} else if (value.compareTo(new BigDecimal("1")) >= 0) {
			numberFormat.setMaximumFractionDigits(4);
		} else if (value.compareTo(new BigDecimal("0.001")) >= 0) {
			numberFormat.setMaximumFractionDigits(6);
		} else {
			numberFormat.setMaximumFractionDigits(8);
		}

		return numberFormat.format(value);
	}

}
