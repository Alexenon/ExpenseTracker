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
          {value: 15, name: 'Drama'},
          {value: 30, name: 'Scifi'},
          {value: 16, name: 'Criminals'},
          {value: 3, name: 'Horror'}
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

function getJsonData(url) {
  return fetch(url)
    .then(response => response.json())
    .then(jsonData => jsonData)
    .catch(error => {
      console.error('Error fetching JSON:', error);
    });
}

window.printMe = function printMe(url) {
    getJsonData('http://localhost:8080/api/expense/all')
      .then(data => {
        console.log('JSON data:', data);

        // Rest of code

      })
      .catch(error => {
        console.error('Error:', error);
      });
}