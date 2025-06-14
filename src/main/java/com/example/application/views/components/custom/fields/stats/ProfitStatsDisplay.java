package com.example.application.views.components.custom.fields.stats;

import com.example.application.utils.common.number.DecimalFormatter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;

public class ProfitStatsDisplay extends StatsComponent implements HasColorfulValue {

    public ProfitStatsDisplay(String label, String value) {
        this(new Paragraph(label), new Paragraph(value), null);
    }

    public ProfitStatsDisplay(String label, Component valueComponent) {
        this(new Paragraph(label), valueComponent, null);
    }

    public ProfitStatsDisplay(String label, String value, String tooltipText) {
        this(new Paragraph(label), new Paragraph(value), tooltipText);
    }

    public ProfitStatsDisplay(String label, double value, DecimalFormatter formatter) {
        this(new Paragraph(label), new Paragraph(formatter.format(value)), null);
    }

    public ProfitStatsDisplay(String label, double value, DecimalFormatter formatter, boolean withColor) {
        this(new Paragraph(label), new Paragraph(formatter.format(value)), null);
        if(withColor)
            setColorClassName(value);
    }

    public ProfitStatsDisplay(String label, Component valueComponent, String tooltipText) {
        this(new Paragraph(label), valueComponent, tooltipText);
    }

    public ProfitStatsDisplay(Component labelComponent, Component valueComponent, String tooltipText) {
        super(labelComponent, valueComponent, tooltipText);
        addClassName("result-item");
        labelComponent.addClassName("stats-title");
        valueComponent.addClassName("stats-item");
    }

}