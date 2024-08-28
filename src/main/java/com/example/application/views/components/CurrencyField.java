package com.example.application.views.components;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;

public class CurrencyField extends TextField {

    public CurrencyField() {
        this(null);
    }

    public CurrencyField(String label) {
        super(label);
        setAllowedCharPattern("[0-9.]");
        setPrefixComponent(new Paragraph("$"));

        NumeralFieldFormatter formatter = new NumeralFieldFormatter.Builder()
                .delimiter(",")
                .decimalMark(".")
                .decimalScale(2)
                .nonNegativeOnly(true)
                .stripLeadingZeroes(true)
                .build();


        formatter.extend(this);
    }

    public double doubleValue() {
        try {
            return Double.parseDouble(this.getValue().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setValue(double value) {
        this.setValue(String.valueOf(value));
    }

    public static void main(String[] args) {
        System.out.println(formatValue(0));
        System.out.println(formatValue(5));
        System.out.println(formatValue(0.5));
        System.out.println(formatValue(0.567));
        System.out.println(formatValue(0.9233458));
        System.out.println(formatValue(0.000023));
        System.out.println(formatValue(1.233566));
        System.out.println(formatValue(23.338596));
        System.out.println(formatValue(3558.93745));
    }

    private static String formatValue(double value) {
        String valueStr = String.valueOf(value);
        int decimalIndex = valueStr.indexOf('.');

        if (decimalIndex == -1) {
            // If there's no decimal point, return the value as is
            return valueStr;
        }

        String formattedValue;

        if (decimalIndex >= 2) {
            // 2+ digits before the decimal, keep 2 digits after the decimal
            formattedValue = String.format("%.2f", value);
        } else if (decimalIndex == 1) {
            // 1 digit before the decimal, keep 3 digits after the decimal
            formattedValue = String.format("%.3f", value);
        } else {
            // 0 digits before the decimal
            int firstNonZeroAfterDecimal = 0;
            for (int i = decimalIndex + 1; i < valueStr.length(); i++) {
                if (valueStr.charAt(i) != '0') {
                    firstNonZeroAfterDecimal = i - decimalIndex;
                    break;
                }
            }

            if (firstNonZeroAfterDecimal == 1) {
                // First non-zero digit is right after the decimal point, keep 4 digits
                formattedValue = String.format("%.4f", value);
            } else {
                // Keep `n` digits after the decimal, where `n` is the position of the first non-zero digit
                formattedValue = String.format("%." + (firstNonZeroAfterDecimal + 2) + "f", value);
            }
        }

        return formattedValue;
    }


    public void setFormatter(NumeralFieldFormatter formatter) {
        formatter.extend(this);
    }

}
