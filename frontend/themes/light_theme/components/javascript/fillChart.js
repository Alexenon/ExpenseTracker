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
window.fillChartPie = async function fillChartPie(url) {
  const jsonObject = await getJsonData(url);
  const dom = document.getElementById('chart-pie');
  const myChart = echarts.init(dom);

  // DESIGN IDEAS FOR CHARTS
  // https://echarts.apache.org/examples/en/editor.html?c=pie-nest - Black border
  // https://echarts.apache.org/examples/en/editor.html?c=bar-nest - White border

  var option = {
    title: {
      text: 'Expenses Chart',
      subtext: 'Click on legend to remove a category',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter(params) {
        return `${params.marker}${params.name}: <b>${params.value} MDL</b> (${params.percent}%)`;
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
  showChartDetails('//div[@id=\"chart-pie\"]');

// UNDER DEVELOPMENT
//  var xc = getElementByXPath('//div[@id=\"chart-pie\"]', '//div[last()]');
//  console.log(xc.textContent);
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
      const pattern = '{name}: {value} MDL ({percent}%)';
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

export function greet(name) {
  console.log(`Hello, ${name}!`);
}