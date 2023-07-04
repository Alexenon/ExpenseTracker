/**
 * Retrieves a parameter from a formatted string using a formatter pattern.
 *
 * @param {string} string - The input string.
 * @param {string} pattern - The formatter pattern.
 * @param {number} paramIndex - The index of the parameter to retrieve.
 * @returns {string|null} The parameter value or null if not found.
 */
function getParamFormatter(string, pattern, paramIndex) {
    const params = pattern.match(/\w+/g);
    const escapeRegex = (string) => string.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    const formatterParts = pattern.split(/\{[^}]+\}/g);
    const values = string.split(new RegExp(formatterParts.map(escapeRegex).join("(.+)")));

    return values?.[paramIndex]?.trim();
}


window.printMe = function printMe(url) {
    getJsonData(url)
      .then(data => {
        console.log('JSON data:', data);

        // Rest of code

      })
      .catch(error => {
        console.error('Error:', error);
      });
}


/**
 * Makes a GET request to the specified URL and returns a JSON object.
 *
 * @param {string} url - The URL to fetch the JSON data from.
 * @returns {Object|null} - A JSON object from the provided URL, or null if there's an error.
 */
window.getJsonData = async function getJsonData(url) {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Failed to fetch data');
    }
    const jsonData = await response.json();
    return jsonData;
  } catch (error) {
    console.error('Error fetching JSON:', error);
    return null;
  }
}


/**
 * Updates the pie chart with id = 'chart-pie'
 * with json data from http response on provided URL
 */
window.fillChartPie = async function fillChartPie() {
  const dom = document.getElementById('chart-pie');
  const myChart = echarts.init(dom);
  const jsonObject = await getJsonData('http://localhost:8080/api/expense/grouped');
//  const jsonObject = await getJsonData('http://localhost:8080/api/expense/grouped?month=6');

  var option = {
    title: {
      text: 'Expenses Chart',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b} : {d}%'
    },
    legend: {
      bottom: 10,
      left: 'center',
      // Think about this
//    left: 'left',
//    orient: 'vertical',
      data: jsonObject.map(item => item[0])
    },
    series: [
      {
        name: 'Expenses by category:',
        type: 'pie',
        radius: '60%',
        center: ['50%', '50%'],
        selectedMode: 'single',
        itemStyle: {
          borderWidth: 0,
          borderColor: '#000000'
        },
        data: jsonObject.map(item => {
          return { name: item[0], value: item[1] };
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        showEmptyCircle: true,
        emptyCircleStyle: {
            opacity: 1,
            color: 'lightgray'
        },
        label: {
          normal: {
            formatter: '{b} : {c} MDL',
            position: 'outside'
          }
        }
      }
    ]
  };

  if (jsonObject.length === 0) {
    option.series[0].data = [{ name: "Empty", value: 0 }];
    option.series[0].itemStyle.borderWidth = 1;
    option.series[0].color = ["#d3d3d3"];
    option.tooltip.formatter = '{c} MDL';
  }

  /* https://github.com/apache/echarts/issues/3264

    itemStyle: {
        normal: {
            borderColor: '#fff',
            borderWidth: '0'
        },
        emphasis: {
            borderColor: '#fff',
            borderWidth: '0'
        }
    },
  */

  if (option && typeof option === 'object') {
    myChart.setOption(option, true);
  }

  showChartDetails('//div[@id=\"chart-pie\"]');

  var xc = getElementByXPath('//div[@id=\"chart-pie\"]', '//div[last()]');
  console.log(xc.textContent);
};


/**
 * This function take the information displayed on element from pie chart
 * that users clicks, and displays more information using expense.id
 */
function showChartDetails(xpath) {
  const chartElement = document.evaluate(
    xpath + '//canvas',
    document,
    null,
    XPathResult.FIRST_ORDERED_NODE_TYPE,
    null
  ).singleNodeValue;

  const elementWithText = document.evaluate(
      xpath + '//div[last()]',
      document,
      null,
      XPathResult.FIRST_ORDERED_NODE_TYPE,
      null
  ).singleNodeValue;

  if (chartElement && elementWithText) {
    chartElement.addEventListener('click', function() {
      const pattern = '{b} : {d}%';
      const text = elementWithText.textContent;
      var expenseName = getParamFormatter(text, pattern, 1);
      console.log(expenseName);
    });
  }
};

function getElementByXPath(xpath, childXpath) {
  return document.evaluate(
      xpath + childXpath,
      document,
      null,
      XPathResult.FIRST_ORDERED_NODE_TYPE,
      null
  ).singleNodeValue;
}
