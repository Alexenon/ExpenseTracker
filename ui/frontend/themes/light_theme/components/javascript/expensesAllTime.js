/**
 * This function displays information about all expenses stored
 * and displays them using a chart.
 */
window.fillChart = function fillChart() {
    var chartDom = document.getElementById('yearly-chart');
    var myChart = echarts.init(chartDom);

    var option = {
      dataset: {
        source: [
          ['amount', 'product'],
          [58212, 'Matcha Latte'],
          [78254, 'Milk Tea'],
          [41032, 'Cheese Cocoa'],
          [12755, 'Cheese Brownie'],
          [20145, 'Matcha Cocoa'],
          [79146, 'Tea'],
          [91852, 'Orange Juice'],
          [101852, 'Lemon Juice'],
          [20112, 'Walnut Brownie']
        ]
      },
      grid: { containLabel: true },
      xAxis: { name: 'amount' },
      yAxis: { type: 'category' },
      visualMap: {
        orient: 'horizontal',
        left: 'center',
        min: 0,
        max: 100000, // TODO: map to max value from data[]
        text: ['High Score', 'Low Score'], // TODO: Change text for min and max values
        dimension: 0,
        inRange: {
          color: ['#65B581', '#FFCE34', '#FD665F']
        }
      },
      series: [
        {
          type: 'bar',
          encode: {
            x: 'amount',
            y: 'product'
          },
          // TODO: Add CURRENCY sign for tooltip and label
          //  currently this doesn't work properly
          endLine: {
            show: true,
            formatter: '$'
          },
          // showBackground: true,
          emphasis: {
            label: {
              show: true,
              distance: 10,
              fontStyle: 'oblique',
              fontWeight: 'bolder',
              fontFamily: 'Courier New',
              fontSize: 25,
              position: 'right'
            }
          }
        }
      ]
    };

    option && myChart.setOption(option);
    window.addEventListener('resize', myChart.resize);
}