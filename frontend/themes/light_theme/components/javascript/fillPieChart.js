import { getJsonData } from './utils.js';
import { getElementByXPath } from './utils.js';
import { getParamFormatter } from './utils.js';

/**
 * Updates the pie chart with json data
 */
window.fillExpensesChart = function fillExpensesChart(jsonDataString) {
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
                        <p class="tooltip-category">${params.name}</p>
                        <p> : </p>
                        <p class="tooltip-value">${params.value}</p>
                        <p class="tooltip-currency">MDL</p>
                        <div class="tooltip-percent">${params.percent}%</div>
                    </div>
                `;
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
            element.setAttribute('data-selected', selectedItem);
        } else {
            element.removeAttribute("data-selected");
        }
    });

    chart.on('legendselectchanged', function (params) {
        const legendHiddenItems = Object.entries(params.selected)
            .filter(([_, value]) => !value)
            .map(([key]) => key);

        element.setAttribute('legend-hidden', JSON.stringify(legendHiddenItems));
    });

};



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


window.fillAssetsDiversityChart = function fillAssetsDiversityChart(jsonDataString) {
    const jsonDataObject = JSON.parse(jsonDataString);
    const element = document.getElementById('chart-pie');
    const chart = echarts.init(element);

    var option = {
        tooltip: {
            trigger: 'item',
            className: 'chart-tooltip',
            formatter(params) {
                return `
                    <div class="tooltip-content">
                        ${params.marker}
                        <p class="tooltip-category">${params.name}</p>
                        <div class="tooltip-percent">${params.percent}%</div>
                    </div>
                `;
            }
        },
        legend: {
            top: '5%',
            left: 'center'
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                selectedMode: 'single',
                radius: ['45%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 15,
                        fontWeight: 'bold',
                        className: 'chart-label',
                        formatter: function (params) {
                            console.log('params', params);
                            return `${params.name} $${params.value} ${params.percent}%`;
                        }
                    }
                },
                labelLine: {
                    show: false
                },
                data: jsonDataObject
            }
        ]
    };

    // Setting chart style when chart is empty
    if (jsonDataObject.length === 0) {
        option.legend.show = false;
        // option.tooltip.formatter = '0 MDL';
        option.series[0].color = 'lightgray';
        option.series[0].data = [{ name: "Empty", value: 0 }];
    }

    option && chart.setOption(option);
    window.addEventListener('resize', chart.resize);
};



// ------------------------------



//option = {
//    tooltip: {
//        trigger: 'item',
//        formatter(params) {
//            return `
//        <div class="tooltip-content">
//            ${params.marker}
//            <p id="selected-category" class="tooltip-category">${params.name}</p>
//            <div class="tooltip-percent">${params.percent}%</div>
//        </div>
//        `;
//        }
//    },
//    legend: {
//        top: '5%',
//        left: 'center'
//    },
//    series: [
//        {
//            name: 'Access From',
//            type: 'pie',
//            selectedMode: 'single',
//            radius: ['45%', '70%'],
//            avoidLabelOverlap: false,
//            itemStyle: {
//                borderRadius: 10,
//                borderColor: '#fff',
//                borderWidth: 2
//            },
//            label: {
//                show: false,
//                position: 'center'
//            },
//            emphasis: {
//                label: {
//                    show: true,
//                    fontSize: 15,
//                    fontWeight: 'bold',
//                    formatter: function (params) {
//                        console.log('params', params);
//                        return `${params.name} $${params.value} ${params.percent}%`;
//                    }
//                }
//            },
//            labelLine: {
//                show: false
//            },
//            data: [
//                { name: 'BTC', value: 600 },
//                { name: 'ETH', value: 300 },
//                { name: 'SOL', value: 250 },
//                { name: 'BNB', value: 189 },
//                { name: 'ALT', value: 100 }
//            ]
//        }
//    ]
//};
