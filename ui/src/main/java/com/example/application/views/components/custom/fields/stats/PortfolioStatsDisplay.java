package com.example.application.views.components.custom.fields.stats;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;

public class PortfolioStatsDisplay extends StatsComponent {

    public PortfolioStatsDisplay(String label, String value) {
        this(new Paragraph(label), new Paragraph(value), null);
    }

    public PortfolioStatsDisplay(String label, Component valueComponent) {
        this(new Paragraph(label), valueComponent, null);
    }

    public PortfolioStatsDisplay(String label, String value, String tooltipText) {
        this(new Paragraph(label), new Paragraph(value), tooltipText);
    }

    public PortfolioStatsDisplay(String label, Component valueComponent, String tooltipText) {
        this(new Paragraph(label), valueComponent, tooltipText);
    }

    public PortfolioStatsDisplay(Component labelComponent, Component valueComponent, String tooltipText) {
        super(labelComponent, valueComponent, tooltipText);
        addClassName("portfolio-stats-details");
        labelComponent.addClassName("stats-title");
        valueComponent.addClassName("stats-item");
    }



}
