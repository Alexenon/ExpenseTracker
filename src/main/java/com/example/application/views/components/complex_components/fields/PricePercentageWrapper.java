package com.example.application.views.components.complex_components.fields;

import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.utils.common.number.DecimalFormatter;
import com.example.application.views.components.complex_components.PercentageBadge;
import com.example.application.views.components.complex_components.ProfitValueParagraph;
import com.vaadin.flow.component.html.Div;

/**
 * Component to display both Price and Percentage values using a single component
 */
public class PricePercentageWrapper extends Div {

    private final ProfitValueParagraph price;
    private final PercentageBadge percentage;

    CurrencyFormatter currencyFormatter = new CurrencyFormatter();

    public PricePercentageWrapper(double priceValue, double percentageValue) {
        price = new ProfitValueParagraph(priceValue, currencyFormatter, true);
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
