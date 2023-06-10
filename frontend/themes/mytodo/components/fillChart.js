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
        radius: '75%',
        center: ['50%', '50%'],
        selectedMode: 'single',
        data: [
          {value: 15, name: 'Drama'},
          {value: 30, name: 'Scifi'},
          {value: 16, name: 'Crime'},
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
