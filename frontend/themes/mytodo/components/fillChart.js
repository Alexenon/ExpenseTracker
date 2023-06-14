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


/**
 * Makes a GET request to the specified URL and returns a JSON object.
 *
 * @param {string} url - The URL to fetch the JSON data from.
 * @returns {Promise<Object|null>} - A promise that resolves to the JSON object, or null if there's an error.
 */
function getJsonData(url) {
  return fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to fetch data');
      }
      return response.json();
    })
    .catch(error => {
      console.error('Error fetching JSON:', error);
      return null;
    });
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


window.fillChartPie = function fillChartPie() {
  var dom = document.getElementById("chart-pie");
  var myChart = echarts.init(dom);
  var app = {};

  var option = {
    title: {
      text: 'Expenses Chart',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b} : {c} ({d}%)'
    },
    legend: {
      bottom: 10,
      left: 'center',
      data: ['Drama', 'Scifi', 'Crime', 'Horror']
    },
    series: [
      {
        name: "Expenses by category",
        type: 'pie',
        radius: '60%',
        center: ['50%', '50%'],
        selectedMode: 'single',
        data: [
          {value: 4, name: 'Food and Drinks'},
          {value: 4, name: 'Fuel'},
          {value: 6, name: 'Distractions'},
          {value: 3, name: 'Other'}
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          normal: {
            formatter: '{b} : {c}',
            position: 'outside'
          }
        },
      }
    ]
  };

  if (option && typeof option === "object") {
    myChart.setOption(option, true);
  }
};


/**
 * This function take the information displayed on element from pie chart
 * that users clicks, and displays more information using expense.id
 */
window.showChartDetails = function showChartDetails(xpath) {
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
      console.log(elementWithText.textContent);
    });
  }
};