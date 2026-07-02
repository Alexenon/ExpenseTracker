package com.example.application.views.components.custom.fields.stats;

import com.example.application.utils.formatters.DecimalFormatter;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.shared.Tooltip;

/**
 * Component designed to represents a specific stat for an item -> Label + Value
 * */
public class StatsComponent extends Div {

    private Component labelComponent;
    private Component valueComponent;

    public StatsComponent() {
        this(new Paragraph(), new Paragraph());
    }

    public StatsComponent(String label, String value) {
        this(new Paragraph(label), new Paragraph(value));
    }

    public StatsComponent(String label, String value, String tooltipText) {
        this(new Paragraph(label), new Paragraph(value), tooltipText);
    }

    public StatsComponent(String label, Component valueComponent) {
        this(new Paragraph(label), valueComponent);
    }

    public StatsComponent(String label, Component valueComponent, String tooltipText) {
        this(new Paragraph(label), valueComponent, tooltipText);
    }

    public StatsComponent(Component labelComponent, Component valueComponent) {
        this(labelComponent, valueComponent, null);
    }

    public StatsComponent(Component labelComponent, Component valueComponent, String tooltipText) {
        this.labelComponent = labelComponent;
        this.valueComponent = valueComponent;
        add(labelComponent, valueComponent);
        addLabelTooltip(tooltipText);
    }

    public void setLabel(Component labelComponent) {
        this.labelComponent = labelComponent;
    }

    public void setLabelText(String text) {
        if (labelComponent instanceof HasText hasText) {
            hasText.setText(text);
        } else {
            throw new IllegalStateException("labelComponent does not support adding text");
        }
    }

    public String getLabelText(String text) {
        return labelComponent instanceof HasText hasText ? hasText.getText() : "";
    }

    public void setValueComponent(Component valueComponent) {
        this.valueComponent = valueComponent;
    }

    public void setValue(double value) {
        setValue(String.valueOf(value));
    }

    public void setValue(double value, DecimalFormatter formatter) {
        setValue(formatter.format(value));
    }

    public void setValue(String text) {
        if (valueComponent instanceof HasText hasText) {
            hasText.setText(text);
        } else {
            throw new IllegalStateException("valueComponent does not support adding text");
        }
    }

    public String getValue(String text) {
        return valueComponent instanceof HasText hasText ? hasText.getText() : "";
    }

    public Component getLabelComponent() {
        return labelComponent;
    }

    public Component getValueComponent() {
        return valueComponent;
    }

    public void addLabelTooltip(String tooltipText) {
        if (tooltipText == null || tooltipText.isEmpty()) {
            return;
        }

        MonoIcon infoIcon = PictogramIcon.INFORMATION_OUTLINE.create();
        Tooltip tooltip = Tooltip.forComponent(infoIcon)
                .withText(tooltipText)
                .withPosition(Tooltip.TooltipPosition.TOP);

        if (labelComponent instanceof HasComponents hasComponents) {
            hasComponents.add(infoIcon);
        }
    }

}
