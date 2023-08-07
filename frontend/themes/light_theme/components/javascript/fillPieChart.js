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
    var chartTextElement = getElementByXPath(chartXpath + '//div[last()]');

    chartElement.addEventListener('click', function() {
        var text = chartTextElement.textContent;
        var expenseName = getParamFormatter(text, patternTooltip, 1);
        console.log(expenseName);
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
      formatter(params) {
        // TODO: Upgrade this formatter to have DOM elements with classes
        return `${params.marker}${params.name}: <b>${params.value} MDL</b> <div class="triangle"> (${params.percent}%) </div>`;
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
  showChartDetails();
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
