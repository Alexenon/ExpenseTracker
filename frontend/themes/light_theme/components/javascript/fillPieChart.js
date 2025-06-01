import { getJsonData } from './utils.js';
import { getElementByXPath } from './utils.js';
import { getParamFormatter } from './utils.js';

/**
 * Updates the pie chart with json data
 */
window.fillExpensesChart = function fillExpensesChart(jsonDataString) {
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
                        <p class="tooltip-category">${params.name}:</p>
                        <p class="tooltip-value">${params.value}</p>
                        <p class="tooltip-currency">MDL</p>
                        <div class="tooltip-percent">${params.percent}%</div>
                    </div>
                `;
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
    if (jsonDataObject.length === 0) {
        option.legend.show = false;
        option.tooltip.formatter = '0 MDL';
        option.series[0].color = 'lightgray';
        option.series[0].data = [{ name: "Empty", value: 0 }];
    }

    option && chart.setOption(option);
    window.addEventListener('resize', chart.resize);

    // Adds 'data-selected' attribute for selected chart item
    chart.on('selectchanged', function (params) {
        if (params.fromAction == 'select') {
            let selectedIndex = params.selected[0].dataIndex[0];
            let selectedItem = option.series[0].data[selectedIndex].name;
            element.setAttribute('data-selected', selectedItem);
        } else {
            element.removeAttribute("data-selected");
        }
    });

    chart.on('legendselectchanged', function (params) {
        const legendHiddenItems = Object.entries(params.selected)
            .filter(([_, value]) => !value)
            .map(([key]) => key);

        element.setAttribute('legend-hidden', JSON.stringify(legendHiddenItems));
    });

};



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/*


https://echarts.apache.org/examples/en/editor.html?c=line-gradient&code=PYBwLglsB2AEC8sDeBYAULWZjADaRAC5l1NMwAnCAc2oFMLiByCMOgWyYBpSyAzYBXYBDMGwoAKEMIrD2AZwCUJDGUwU6YAK4U4AA15rMAHgAmEAG6wAxrmHz58AETY8BALTWYbaGCcA-QyM1ABIkaVkFADoRCgBrBgBfIOCTEFgIU2d5Olw6azZTT1E6akEATycbOwdnV3wIEGK2MopK_zCIuXko6Dk6ROMAehBA1VSyM0tq-0cXHAamkAZrOl8AzpluqOWKVd9EgFJh8wsxiZPLc9S9AG4g5NVEnlU8-mhTYlRx2EEINbAzAsDEg1mEuG4QWwRFgTAATABWQ6Qn55PiA2EABmRLzUQyGsByeQKdFMAFlgKY6MRKFo6LxnrwclQ6PJiABtILfYJ9djU2EAQWsqwcsAAYhRgJxcUYwOVlswQP8UcE0RimNiVUYiflChSqcx5BBoNQ8lq1LJzFo2bB2UwACxI7iwgDsmoAujK1MILMBMgAZYQAI1yAHlgRQ7DC-OCcl6yKwOABlOV5L4pMhBwRUigAJWEVptCPjaizFBzAGE8IJmABiPgN81GMs5gDqmTAAAtiHCM4yfpgdSTTKGGzkMQBGTEl2B2EO4dMDsjyTvAADuNIodJnmHxIGARsgMGY-3ETD7M44IE79ggNu5qTnuUXE0Hq43WC3dB3_G8SYgABe_ITsWGZqLgxp0AAEnQNCdhicJwj-mACEIojiMQfBaNABRQHAUhbAoygPq-NgwPIYDILAvLfrAFjgtusC7KesCJAgTGEfI9xLsEGjaLotpgTcSDyOU7BZrgAA-YQ0YkiR6MhRh6Eg2BgOC0lhPRuB0nJClCcEynMQCwj0NJ4QrACRzyfpZDulEABWfrQBITAADrQEwijcaR_akVQ1jdiopGDmJEkvsFmBeLgNawkGdjWHE548UYvnBap4LhRFAi-P-QE9tONlqNlYCtrB1DwcwEmmElEWpaRRm-CZ_IkcFxW5cBSGFWQUUxUw1CyOUNXBY8PlCSNKUzqYojCByQlINR_TMAAQgAKhWzpaXSxAAGyYpirE_vNNHMAAoitUEbQx_IAMx7Qdc0LXyzBJqG_qXdp_KIvtdVGEdi2wktAByS3vVtsATgAHAAnPdyV_U9gr-itoPAXd41qO6Dy8JjaCJNx6DsOUFY3hQYBRDALnsMA1p0MAEbOlhOFHvhIDEbwXjQPIeB0FE0XUFIXm8OgiReUAA
https://echarts.apache.org/examples/en/editor.html?code=PYBwLglsB2AEC8sDeAoWtJgDYFMBcya6GOAHmAQOSAcdoPBGgnLGAUroAbKgK9aVEC-ANEWMMCyQQBVMQwAnCAHNpOCVQhgcAWw7oeRXHOgATUUXRTpACwqxKARgAMAUkq9xwKTmjnKAN3mQAxgEMsB0MMUCpLAFZ7R2IAZxxcH2VdAFlgXXxYADMAuOCAejziMABPEAzKGJ8JQUDozXQ4lxiCAG1gsXFYaD8VcqZAbgNAQuigzthcTPNraPF-EVgp4PQSsqoQCBwRzok_XQgAV2bYFsoAZjsHCwAWc4BdaeI_D2AIXQAZPwAjeIB5LwksPxzbJYOLcWAFQBg6oB650AAOmAQMjAH3RgDt_QBccsDcqMlKoAMolXAGUboD7OdISADCgmcVAAxJk6ZtRsSJKSAOovMAmAgAJkWsHqnR8rmUCiOlC5lyiFjO9juvIBXywBMJMRMwAA7gR0Th7uIQMAYkooNAqIK3PIGZ1Ms4VH4wMLNXtoIkjbAABQgPzbFQxACUhEJxAkODAewkcAABkgPmAAD4DQacWAAHTgSF0sYAJEgPV6YgA6DwBPY4TiccMAbl5Gh1xCwEGgOAAEjgZGZuQA2GtGCA-Tn-gOwaNKgewHyUkWUak-XTT6fqAf8gPp4cDq1ubEQABeGXFXctMDALJbpncxKwunnAc4Vb5vMXtc-8Ve9YyHVGKvVmpyODvNdUIBMPwDUON9OnleIV3fVUNSyb8b2vUZ7waeIcESSCHwVdDxA_GCwAkYt4N_Xk4gSJJUnSKgDWgaRcAtZDSJwXRvjpOJzBsGtdFtPxWhvJBYELLBiwIGxLgADjBbpeioOhLAuZYMksPk9z4gShNgM4pi6HpyjoLk5NKHclN4_iiwyS4uU0ySdNoE59JWdSjIDFTTOE6wxIk7TpNoS47LMxzCWcwSMg0jypIsOhwl8ghwn80ZArU8zLM88LaHbKLYE7WASNQpICDw4tb1GG5ggQ2Bis4SsUAAaxwHAQAQWBQEgGA80adYYhaawbjzTiwD8PNMnrXQAEk9DIV1XWAP14AAPka1qUMSRifUrFRijJQCJDAPMYFdCpFrAXs_GoxiLkyR1nRgN0QD9DoCjHaAYkEHA8ywYBpFdAAiQAzyMAReVAEJrQAHU3geBPu4G7K3QCBMmugbqhUABBS64BBixssSSg_RquqGpABaGN0Trut6vxRvSUgich2BodhzJ4aR5qUcQShHXRsBMeCNaNs9bbdhiD1DpMBmjVdUClgMyiDrorL5HasmyAIBZRhJ-XSAIbGQBKlaUE4FagA
https://echarts.apache.org/examples/en/editor.html?c=line-gradient&code=G4QwTgBA9gDgLgSygOwgXggbwFAQouAGwFMAuLXPfYgDznIHIAFKMOAMykKQgBEFgxMAGcEcAJ4MANJTxgEAcwAW9CAwDGxZHCENKAXxl44ULohjkcVfPIUKhjMcQC202RE5hnIODrAAKGHAQZ2EASgprOWI4AFcwVAADd2sAHgATAQh1QhBhYTQAIhMzBBgAWnUUHW1CgD4UqLwAEkwgsBDhADpvMABrIX1GptSYCAR0ouFiEnUddMqfYgVWcULs3PyiksJzRZ0VsDW61vbOruQQ4n1UgHoYBqaRzOANvILi012y8pghTVqJzawVCXT-YABcH0AFI7i9Hk84QIEU1EgBudxDPCGSgkezIdKWdysBBaVQMQRsBDqECENzWEwWNQAJgArND6VQSOxyQAGDlGKg6OgAZQkJCJTU42hFCAAXmQIABGABsUggt1uEAAksh1GBiHliB5qhBRAqTZA8Vp0uMdKFGtK4AB1YiKFSMABGXHSDA1WoA8vAkJdCOqTBBvAN8EpjcK4BBvYR0pj3JrprN5gBZKDpRVwMCxYgGQXTeTEYTkADa7isUUuzkVDAAgupNPkIAAxMBQVyChniP6MGCkznWbl8gWNDPEObEdI5vOMUTIBQkMdUDqZWKViBVhgAFnZ0jUAHZ-QwALr9qiQhx7hisi_qx8X6-NEDAKATAAyIE9MwBpSuRMuwtLTDeeBOM4YriBKkRPN6YB5mAABKIDbrurKQVQSEoQAwlwrCMAAxOw5EbtYeFCM6ExwEo5DMo0OJNDOc7pAG5HTKoSq8jhuQAYQkpPMIShQAA7uQBZFjheAwFAoiICgjB3mAlFUE6soKuQSrYcMEDcMgxAABJusoqjMsysmWt4vj3uwsR6kpqCBCC4QIU82QoMICaYBADbEOqoCEDJEDgpCED6OgYVuRinnRHECR7vp1iJJgwjiM4SYAD6tAF-j6Ik1mpZgJhwLSuWtMFRYFUVKVUGl4VkiA9i5W0_xkjChX1ZeXQAFbfsg_gMAAOsgDBhHFTwsU88jqAxHkiZlSbCfFXmEMRaierk6h9Hoa0zZ5ZW0qt8WafKirMnx9V4E6rruuSSa-vVh1PE12gtYqdZred2nKlZN3rZtDAKB0kgvfpWJRK9eAuDASh5Agu7fVEAkzKdrFiZJHjgYF9W_YqunFQZCBGaZD2MQDP2sLZfjkA5TkhhArkdKEEQo55VTID5WD-VcQW0qF70JlFGBnKEU1rQaiWoDWa0lRlWVcG1-WFcTJXHYQlWYNV1xq4DeCNR1H2tacxtQtC3XyxAvUDaTw1jRNkvTcTc0LRzS1K0Ji1rVUG1gF6O17YDMNPJrGNndUWmXdd1u3dU93mV6Pr7fLodNMLn0R55BM6VTcd-8DoMgOD1tQ555fQ8xOHpD4IDVvpfkBYwABCAAq-Enrr5AAJy8rykXWU3VytwAci3XeC4qKr94Pjd842jAAKJt8Zk8hYqADMs_p7zzdqCKAY_uvRaMU-c-ecPi9qM2P5tyfirn5XeCXqmL_YPoGLYM44j4QjbBdBQMNZwUAdzECgJSE8DM5hMxZp0dmlAubCC4MQLoG0FBwLZlNWAzkuhllJMIKsvJepwwRqIboaNCB4KxtFaSxBsHBhQHgoQBCiG9UodQiS0UwKEGmFNH-f9wBwGYXAIMzl_A4JDJND-0jv6_3_sIoBDA2JwHmiAVc84oGORgSgZm4t3J1kkUw_BFY2FdFIYjCh_4ZicPEtw3GDDcEmMIcQtB1iqGiS4RgHhfDKACIUSIsRIYJGMOQNI_Qk0gA&version=5.5.1

*/
window.fillAssetsDiversityChart = function fillAssetsDiversityChart(jsonDataString) {
    const jsonDataObject = JSON.parse(jsonDataString);
    const element = document.getElementById('assets-diverstity-chart');
    const chart = echarts.init(element);

    var option = {
        tooltip: {
            trigger: 'item',
            formatter(params) {
                return `
                    <div class="tooltip-content">
                        ${params.marker}
                        <p class="tooltip-category">${params.name}</p>
                        <div class="tooltip-percent">${params.percent}%</div>
                    </div>
                `;
            }
        },
        legend: {
            top: '25%',
            left: '0%',
            orient: 'vertical',
            itemWidth: 30,
            itemHeight: 20,
            selectedMode: true,
            textStyle: {
                fontSize: 20,
//                fontWeight: 'bold'
            }
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                center: ['50%', '50%'],
                radius: ['45%', '70%'],
                selectedMode: 'single',
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 5,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 25,
                        lineHeight: 30,
                        formatter: function (params) {
                            const { name, value, percent } = params;
                            return [
                                `{symbol|${name}}`,
                                `{total|$${value}}`,
                                `{percentage|${percent}%}`
                            ].join('\n');
                        },
                        rich: {
                            symbol: {
                                color: 'black'
                            },
                            total: {
                                fontSize: 30,
                                fontWeight: 'bold'
                            },
                            percentage: {
                                fontSize: 22,
                                color: 'gray'
                            }
                        }
                    }
                },
                data: jsonDataObject
            }
        ]
    };

    // Setting chart style when chart is empty
    if (jsonDataObject.length === 0) {
        option.legend.show = false;
        // option.tooltip.formatter = '0 MDL';
        option.series[0].color = 'lightgray';
        option.series[0].data = [{ name: "Empty", value: 0 }];
    } else {
        option.legend.show = true;
        option.series[0].color = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'];
    }

    option && chart.setOption(option);
    window.addEventListener('resize', chart.resize);
};


