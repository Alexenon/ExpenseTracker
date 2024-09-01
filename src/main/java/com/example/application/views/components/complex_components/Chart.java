package com.example.application.views.components.complex_components;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import org.icepear.echarts.Option;
import org.icepear.echarts.render.Engine;

import java.util.Objects;

@Tag(Tag.DIV)
@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/echarts/5.4.2/echarts.min.js")
public class Chart extends Composite<Div> {

    private final String id;
    private final Option option;
    private final Div chartContainer = new Div();

    public Chart(String id, Option option) {
        this.id = Objects.requireNonNull(id);
        this.option = option;
        initialize();
    }

    private void initialize() {
        chartContainer.setId(id);
        chartContainer.setSizeFull();

        attachChartToDOM();
        getContent().add(chartContainer);
    }

    private void attachChartToDOM() {
        Engine engine = new Engine();
        String jsonOption = engine.renderJsonOption(option);

        System.out.println(jsonOption);

        chartContainer.getElement().executeJs("""
                var chart = echarts.init(document.getElementById('%s'));
                var option = %s;
                chart.setOption(option);
                window.addEventListener('resize', chart.resize);
                """.formatted(id, jsonOption));
    }

    public void executeJS(String js) {
        chartContainer.getElement().executeJs(js);
    }

}
