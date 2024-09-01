import { getJsonData } from './utils.js';
import { getElementByXPath } from './utils.js';
import { getParamFormatter } from './utils.js';

const chartXpath = '//div[@id=\"chart-pie\"]';
const patternTooltip = '{name}: {value} MDL ({percent}%)';

/**
 * This function take the information displayed on element from pie chart
 * that users clicks, and displays more information using expense.id
 */
function showChartDetails() {
    var chartElement = getElementByXPath(chartXpath + '//canvas');
    chartElement.addEventListener('click', function() {
        var elem = document.getElementById('selected-category');
        var text = elem.textContent;
        console.log(text);
    });
}

/**
 * Updates the pie chart with id = 'chart-pie'
 * with json data from http response on provided URL
 */
window.fillPie = async function fillPie(url) {
  const jsonObject = await getJsonData(url);
  const dom = document.getElementById('chart-pie');
  const myChart = echarts.init(dom);

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
        data: jsonObject.map(item => {
          return { name: item[0], value: item[1] };
        }),
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
  if (jsonObject.length === 0) {
    option.legend.show = false;
    option.tooltip.formatter = '0 MDL';
    option.series[0].color = 'lightgray';
    option.series[0].data = [{ name: "Empty", value: 0 }];
  }

  option && myChart.setOption(option);
  window.addEventListener('resize', myChart.resize);
  showChartDetails();

  // LISTENER FOR CLICKING ON SERIES PIE
  myChart.on('click', 'series.pie', function (params) {
    console.log(params);
    // console.log(params.data.name);
  });

};

// Printing data from getJsonData
window.printData = function printData(url) {
    getJsonData(url)
      .then(data => {
        console.log('JSON data:', data);
      })
      .catch(error => {
        console.error('Error:', error);
      });
}


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
                <p class="tooltip-currency">USD</p>
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
  if (jsonObject.length === 0) {
    option.legend.show = false;
    option.tooltip.formatter = '0 MDL';
    option.series[0].color = 'lightgray';
    option.series[0].data = [{ name: "Empty", value: 0 }];
  }

  option && chart.setOption(option);
  window.addEventListener('resize', chart.resize);
//  showChartDetails();

  // LISTENER FOR CLICKING ON SERIES PIE
  chart.on('click', 'series.pie', function (params) {
    console.log(params);
    // console.log(params.data.name);
  });

};