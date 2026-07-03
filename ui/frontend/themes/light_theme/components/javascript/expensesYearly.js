// https://echarts.apache.org/examples/en/editor.html?c=dataset-link
// https://codepen.io/beng-starboard/pen/qBJrrbv
// https://codepen.io/marcong95/pen/mdOqeLo

const pieSeries = {
  type: 'pie',
  id: 'pie',
  radius: '30%',
  center: ['50%', '25%'],
  emphasis: {
    focus: 'self'
  },
  label: {
    formatter: '{b}: {@2012} ({d}%)'
  },
  encode: {
    itemName: 'product',
    value: '2012',
    tooltip: '2012'
  }
};

const lineSeries =  {
  type: 'line',
  smooth: true,
  seriesLayoutBy: 'row',
  emphasis: { focus: 'series' }
};

const list = [pieSeries];

for (let i = 0; i < 10; i++) {
  list.push(lineSeries);
}

setTimeout(function () {
  option = {
    legend: {},
    tooltip: {
      trigger: 'axis',
      showContent: true
    },
    dataset: {
      source: [
      ['product', '2010', '2011', '2012', '2013', '2014', '2015', '2016', '2017', '2018', '2019', '2020', '2022', '2023'],
      // Products section
      ['Product 1', 15.5, 10.2, 20.8, 10.6, 12.3, 13.1, 10.5, 8.4, 10.2, 12.1, 13.7, 10.9, 6.3],
      ['Product 2', 20.1, 22.3, 15.7, 13.9, 10.4, 8.8, 10.6, 12.5, 15.2, 18.5, 19.3, 15.8, 8.6],
      ['Product 3', 15.9, 18.4, 20.2, 21.6, 15.8, 13.2, 10.7, 12.6, 10.4, 8.3, 10.1, 12.5, 10.7, 6.4],
      ['Product 4', 5.2, 5.7, 5.3, 5.5, 5.1, 5.8, 5.4, 5.9, 5.6, 5.9, 5.2, 5.7, 5.3, 5.8],
      ['Product 5', 5.5, 5.9, 5.7, 5.4, 5.2, 5.6, 5.8, 5.3, 5.9, 5.5, 5.7, 5.6, 5.3, 5.1],
      ['Product 6', 5.6, 5.2, 5.8, 5.7, 5.4, 5.5, 5.6, 5.9, 5.3, 5.7, 5.2, 5.5, 5.9, 5.8],
      ['Product 7', 5.9, 5.3, 5.6, 5.4, 5.7, 5.1, 5.8, 5.6, 5.9, 5.2, 5.5, 5.3, 5.4, 5.7],
      ['Product 8', 5.1, 5.5, 5.2, 5.6, 5.3, 5.9, 5.7, 5.4, 5.8, 5.3, 5.1, 5.5, 5.7, 5.6],
      ['Product 9', 5.3, 5.6, 5.8, 5.7, 5.4, 5.9, 5.2, 5.5, 5.1, 5.7, 5.3, 5.6, 5.8, 5.9],
      ['Product 10', 15.8, 5.4, 5.2, 5.9, 5.7, 5.3, 5.5, 5.6, 5.4, 5.8, 5.9, 5.2, 5.3, 5.7]
      ]
    },
    xAxis: { type: 'category' },
    yAxis: { gridIndex: 0 },
    grid: { top: '55%' },
    series: list
  };

  myChart.on('updateAxisPointer', function (event) {
    const xAxisInfo = event.axesInfo[0];
    if (xAxisInfo) {
      const dimension = xAxisInfo.value + 1;
      myChart.setOption({
        series: {
          id: 'pie',
          label: {
            formatter: '{b}: {@[' + dimension + ']} ({d}%)'
          },
          encode: {
            value: dimension,
            tooltip: dimension
          }
        }
      });
    }
  });
  myChart.setOption(option);
});



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



option = {
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value'
  },
  tooltip: {trigger:'axis'},
  series: [
    {
      data: [0, 0, 224, 218, 0, 147, 180],
      type: 'line',
      smooth: true
    }
  ]
};

myChart.on('highlight', (p) => {
  myChart.setOption({
    title: {text: 'dataIndex= ' +p.batch[0].dataIndex },
  });
});



