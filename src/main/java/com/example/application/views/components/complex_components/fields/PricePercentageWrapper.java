package com.example.application.views.components.complex_components.fields;

import com.example.application.data.models.NumberType;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.complex_components.ProfitValueParagraph;
import com.vaadin.flow.component.html.Div;

/**
 * Component to display both Price and Percentage values using a single component
 */
public class PricePercentageWrapper extends Div {

    private final ProfitValueParagraph price;
    private final PriceBadge percentage;

    public PricePercentageWrapper(double priceValue, double percentageValue) {
        price = new ProfitValueParagraph(priceValue, NumberType.CURRENCY, true);
        percentage = new PriceBadge(percentageValue, NumberType.PERCENT);
        add(price, percentage);
    }

    public void setPriceColor(boolean shouldBeColored) {
        price.setApplyColor(shouldBeColored);
    }

    public void setPercentageBadgeBackgroundColor(boolean shouldBeColored) {
        if (shouldBeColored) {
            percentage.setBackgroundColor();
        } else {
            percentage.setBackgroundColor(PriceBadge.Color.DEFAULT_BACKGROUND_COLOR);
        }
    }


}
