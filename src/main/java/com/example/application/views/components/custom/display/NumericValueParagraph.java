package com.example.application.views.components.custom.display;

import com.example.application.utils.common.formatters.number.DecimalFormatter;
import com.example.application.views.components.custom.fields.stats.HasColorfulValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Paragraph;

import java.math.BigDecimal;

@Tag(Tag.P)
public class NumericValueParagraph extends Paragraph implements HasColorfulValue {

    private BigDecimal value;
    private boolean withColor;
    private DecimalFormatter formatter;

    public NumericValueParagraph() {
        this(BigDecimal.ZERO, new DecimalFormatter());
    }

    public NumericValueParagraph(BigDecimal value) {
        this(value, new DecimalFormatter());
    }

    public NumericValueParagraph(BigDecimal value, boolean hasColor) {
        this(value, new DecimalFormatter(), hasColor);
    }

    public NumericValueParagraph(BigDecimal value, DecimalFormatter formatter) {
        this(value, formatter, false);
    }

    public NumericValueParagraph(BigDecimal value, DecimalFormatter formatter, boolean hasColor) {
        this.value = value;
        this.formatter = formatter;
        setText(formatter.format(value));
        if(hasColor)
            setColorClassName(value);
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setFormatter(DecimalFormatter formatter) {
        this.formatter = formatter;
        setText(this.formatter.format(value));
    }

}

