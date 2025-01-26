package com.example.application.views.components.custom.fields;

import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.utils.common.number.DecimalFormatter;
import com.example.application.views.components.custom.NumericValueParagraph;
import com.example.application.views.components.custom.PercentageBadge;
import com.vaadin.flow.component.html.Div;

/**
 * Component to display both Price and Percentage values using a single component
 */
public class PricePercentageWrapper extends Div {

    private final NumericValueParagraph price;
    private final PercentageBadge percentage;

    public PricePercentageWrapper(double priceValue, double percentageValue) {
        price = new NumericValueParagraph(priceValue, CurrencyFormatter.withDefaults(), true);
        percentage = new PercentageBadge(percentageValue);
        add(price, percentage);
        addClassName("price-profit-wrapper");
    }

    public void setPriceColor(boolean shouldBeColored) {
        price.setHasColor(shouldBeColored);
    }

    public void setPercentageBadgeBackground(boolean shouldBeColored) {
        percentage.setHasBackground(shouldBeColored);
    }

    public void setPriceFormatter(DecimalFormatter formatter) {
        price.setFormatter(formatter);
    }

    public void setPercentageFormatter(DecimalFormatter formatter) {
        percentage.setFormatter(formatter);
    }

}
