import { getJsonData } from './utils.js';
import { getElementByXPath } from './utils.js';
import { getParamFormatter } from './utils.js';

const chartXpath = '//div[@id=\"chart-pie\"]';
const patternTooltip = '{name}: {value} MDL ({percent}%)';

/**
 * Updates the pie chart with json data
 * TODO: chart example -> https://rb.gy/lety7u
 */
window.fillChartPie = function fillChartPie(jsonDataString) {
    const jsonDataObject = JSON.parse(jsonDataString);
    const element = document.getElementById('chart-pie');
    const chart = echarts.init(element);

    var option = {
        title: {
            text: 'Expenses Chart',
            subtext: 'Click on legend to remove a category',
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            className: 'chart-tooltip',
            formatter(params) {
                return `
            <div class="tooltip-content">
                ${params.marker}
                <p id="selected-category" class="tooltip-category">${params.name}</p>
                <p class="tooltip-value">${params.value}</p>
                <p class="tooltip-currency">MDL</p>
                <div class="tooltip-percent">${params.percent}%</div>
            </div>
        `
            }
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            // This can be removed
            borderRadius: 3,
            borderWidth: 2,
            padding: 10,
            itemGap: 10,
            itemWidth: 25,
            itemHeight: 14,
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                selectedMode: 'single',
                radius: '50%',
                data: jsonDataObject,
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    };

    // Setting chart style when chart is empty
    if (jsonDataObject.length === 0) {
        option.legend.show = false;
        option.tooltip.formatter = '0 MDL';
        option.series[0].color = 'lightgray';
        option.series[0].data = [{ name: "Empty", value: 0 }];
    }

    option && chart.setOption(option);
    window.addEventListener('resize', chart.resize);

    // Adds 'data-selected' attribute for selected chart item
    chart.on('selectchanged', function (params) {
        if (params.fromAction == 'select') {
            let selectedIndex = params.selected[0].dataIndex[0];
            let selectedItem = option.series[0].data[selectedIndex].name;
            console.log("Selected: " + selectedItem);
            element.setAttribute('data-selected', selectedItem);
        }

        if (params.fromAction == 'unselect') {
            element.removeAttribute("data-selected");
        }
    });

};

