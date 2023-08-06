// https://echarts.apache.org/examples/en/editor.html?c=dataset-series-layout-by
// https://echarts.apache.org/examples/en/editor.html?c=dataset-encode0
// https://echarts.apache.org/examples/en/editor.html?c=dataset-link
// https://echarts.apache.org/examples/en/editor.html?c=calendar-vertical

// DEMO CLASS

// https://echarts.apache.org/examples/en/editor.html?c=line-gradient

option = {
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
    max: 100000,
    text: ['High Score', 'Low Score'],
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
      }
    }
  ]
};