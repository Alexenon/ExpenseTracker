package com.example.application.views.components.complex_components.fields;

import com.example.application.data.models.NumberType;
import com.example.application.views.components.complex_components.PercentageBadge;
import com.example.application.views.components.complex_components.ProfitValueParagraph;
import com.vaadin.flow.component.html.Div;

/**
 * Component to display both Price and Percentage values using a single component
 */
public class PricePercentageWrapper extends Div {

    private final ProfitValueParagraph price;
    private final PercentageBadge percentage;

    public PricePercentageWrapper(double priceValue, double percentageValue) {
        price = new ProfitValueParagraph(priceValue, NumberType.CURRENCY, true);
        percentage = new PercentageBadge(percentageValue);
        add(price, percentage);
    }

    public void setPriceColor(boolean shouldBeColored) {
        price.setHasColor(shouldBeColored);
    }

    public void setPercentageBadgeBackgroundColor(boolean shouldBeColored) {
        percentage.setHasBackground(shouldBeColored);
    }


}
