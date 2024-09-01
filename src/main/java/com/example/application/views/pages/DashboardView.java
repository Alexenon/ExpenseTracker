package com.example.application.views.pages;

import com.example.application.views.components.complex_components.Chart;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import jakarta.annotation.security.PermitAll;
import org.icepear.echarts.Pie;
import org.icepear.echarts.charts.pie.PieDataItem;
import org.icepear.echarts.charts.pie.PieSeries;
import org.icepear.echarts.components.legend.Legend;
import org.icepear.echarts.components.title.Title;
import org.icepear.echarts.components.tooltip.Tooltip;

@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@JsModule("./themes/light_theme/components/javascript/fillPieChart.js")
@JavaScript("https://fastly.jsdelivr.net/npm/echarts@5.4.2/dist/echarts.min.js")
public class DashboardView extends Main {

    public DashboardView() {
        addStyle();
    }

    private void addStyle() {
        addClassName("page-content");

        Div chartPie = new Div();
        chartPie.setId("chart-pie");
        testChart();

        HorizontalLayout container = new HorizontalLayout(chartPie);
        container.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        add(container);
    }

    private void testChart() {
        PieDataItem[] data = new PieDataItem[]{
                new PieDataItem().setName("Category A").setValue(1048),
                new PieDataItem().setName("Category B").setValue(735),
                new PieDataItem().setName("Category C").setValue(580),
                new PieDataItem().setName("Category D").setValue(484),
                new PieDataItem().setName("Category E").setValue(300)
        };

        JsonArray jsonOptionData = Json.createArray();
        for (int i = 0; i < data.length; i++) {
            JsonObject jsonObject = Json.createObject();
            jsonObject.put("name", String.valueOf(data[i].getName()));
            jsonObject.put("value", Double.parseDouble(String.valueOf(data[i].getValue())));
            jsonOptionData.set(i, jsonObject);
        }

        UI.getCurrent().getPage().executeJs("console.log($0); fillChartPie($0);", jsonOptionData.toJson());

        System.out.println(jsonOptionData.toJson());
    }


    private void addPieChart() {
        Title title = new Title()
                .setText("Expenses Chart")
                .setSubtext("Click on legend to remove a category")
                .setLeft("center");

        // Create tooltip
        Tooltip tooltip = new Tooltip()
                .setTrigger("item")
                .setClassName("chart-tooltip")
                .setFormatter("""
                        function(params) {
                             return
                                 <div class="tooltip-content">
                                     ${params.marker}
                                     <p id="selected-category" class="tooltip-category">${params.name}</p>
                                     <p class="tooltip-value">${params.value}</p>
                                     <p class="tooltip-currency">MDL</p>
                                     <div class="tooltip-percent">${params.percent}%</div>
                                 </div>
                         }
                         """);


        // Create legend
        Legend legend = new Legend()
                .setOrient("vertical")
                .setLeft("left")
                .setPadding(10)
                .setItemGap(10)
                .setItemWidth(25)
                .setItemHeight(14);


        PieDataItem[] data = new PieDataItem[]{
                new PieDataItem().setName("Category A").setValue(1048),
                new PieDataItem().setName("Category B").setValue(735),
                new PieDataItem().setName("Category C").setValue(580),
                new PieDataItem().setName("Category D").setValue(484),
                new PieDataItem().setName("Category E").setValue(300)
        };


        // Create series
        PieSeries series = new PieSeries()
                .setName("Access From")
                .setType("pie")
                .setSelectedMode("single")
                .setRadius("50%")
                .setData(data);


        // Create pie chart
        Pie pie = new Pie()
                .setTitle(title)
                .setTooltip(tooltip)
                .setLegend(legend)
                .addSeries(series);

        Chart c = new Chart("pie-cool", pie.getOption());
        add(c);
    }


}
