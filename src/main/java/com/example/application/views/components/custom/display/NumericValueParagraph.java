package com.example.application.views.components.custom.display;

import com.example.application.utils.common.formatters.number.DecimalFormatter;
import com.example.application.views.components.custom.fields.stats.HasColorfulValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

@Tag(Tag.P)
public class NumericValueParagraph extends Paragraph implements HasColorfulValue {

    private double value;
    private boolean withColor;
    private DecimalFormatter formatter;

    public NumericValueParagraph() {
        this(0.0, new DecimalFormatter());
    }

    public NumericValueParagraph(double value) {
        this(value, new DecimalFormatter());
    }

    public NumericValueParagraph(double value, boolean hasColor) {
        this(value, new DecimalFormatter(), hasColor);
    }

    public NumericValueParagraph(double value, DecimalFormatter formatter) {
        this(value, formatter, false);
    }

    public NumericValueParagraph(double value, DecimalFormatter formatter, boolean hasColor) {
        this.value = value;
        this.formatter = formatter;
        setText(formatter.format(value));
        if(hasColor)
            setColorClassName(value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setFormatter(DecimalFormatter formatter) {
        this.formatter = formatter;
        setText(this.formatter.format(value));
    }

}

