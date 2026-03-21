package com.example.application.utils.common.formatters.number;

import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DecimalFormatter {

    protected NumberFormat numberFormat = new DecimalFormat();

    public static DecimalFormatter withDefaults() {
        return new DecimalFormatter();
    }

    public DecimalFormatter() {
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMaximumIntegerDigits(10);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String format(Double d) {
        return numberFormat.format(d);
    }

	public String format(BigDecimal d) {
		return numberFormat.format(d);
	}

	public String format(BigInteger d) {
		return numberFormat.format(d);
	}

	@SneakyThrows
    public Number parse(String s) {
        return numberFormat.parse(s);
    }

    public void setGrouping(boolean isGrouped) {
        numberFormat.setGroupingUsed(isGrouped);
    }

    public void setMinimumIntegerDigits(int digits) {
        numberFormat.setMinimumIntegerDigits(digits);
    }

    public void setMaximumIntegerDigits(int digits) {
        numberFormat.setMaximumIntegerDigits(digits);
    }

    public void setMinimumFractionDigits(int digits) {
        numberFormat.setMinimumFractionDigits(digits);
    }

    public void setMaximumFractionDigits(int digits) {
        numberFormat.setMaximumFractionDigits(digits);
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        numberFormat.setRoundingMode(roundingMode);
    }

    public static class Builder {

        private final DecimalFormatter instance;

        public Builder() {
            this.instance = new DecimalFormatter();
        }

        public Builder setGrouping(boolean isGrouped) {
            instance.setGrouping(isGrouped);
            return this;
        }

        public Builder setMinimumIntegerDigits(int digits) {
            instance.setMinimumIntegerDigits(digits);
            return this;
        }

        public Builder setMaximumIntegerDigits(int digits) {
            instance.setMaximumIntegerDigits(digits);
            return this;
        }

        public Builder setMinimumFractionDigits(int digits) {
            instance.setMinimumFractionDigits(digits);
            return this;
        }

        public Builder setMaximumFractionDigits(int digits) {
            instance.setMaximumFractionDigits(digits);
            return this;
        }

        public Builder setRoundingMode(RoundingMode roundingMode) {
            instance.setRoundingMode(roundingMode);
            return this;
        }

        public DecimalFormatter build() {
            return instance;
        }

    }

}
