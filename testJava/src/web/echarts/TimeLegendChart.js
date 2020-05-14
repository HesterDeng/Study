import Common from './TMMSChartCommon.js';
import TMMSChartInterface from "./TMMSChartInterface.js";
import echarts from "echarts"
export default class TimeLegendChart extends TMMSChartInterface{
    constructor(container, isBig = false){
        super(container, isBig);
    }

    getOption(){
        let me = this;
        let option = {
            grid: {
                left: '1%',
                right: '5%',
                top: '4%',
                bottom: '1%',
                containLabel: true
            },
            textStyle: {
                fontSize: '12',
                color: 'white'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    label: {
                        formatter: function (params) {
                            let date = new Date(params.value);
                            let time = echarts.format.formatTime('yyyy/MM/dd hh:mm:ss', date);
                            return time.replace('00:00:00', '');
                        }
                    }
                },
                formatter: function (params) {
                    let result = "";
                    for (let i = 0; i < params.length; i++) {
                        let param = params[0];
                        let axisValue = param.axisValueLabel;
                        let marker = param.marker;
                        let value = param.value[1] ? param.value[1] : 0;
                        let unitStr = 'mm';//me.getMonitorTypeUnit();//监测类型的单位
                        result += axisValue + '<br/>' + marker + value + '<span style="font-style: italic">' + unitStr + '</span>';
                    }
                    return result;
                }
            },
            legend: {
                show: false
            },
            xAxis: {
                type: 'time',
                splitLine: {
                    show: false
                },
                axisLabel: {
                    formatter: function (value) {
                        return echarts.format.formatTime('M/d', new Date(value));
                    }
                },
                axisTick: {
                    length: 0
                },
                minInterval: 3600 * 24 * 1000
            },
            yAxis: {
                type: 'value',
                axisLine: {
                    lineStyle: {color: 'rgba(255,255,255,0.2)'}
                },
                splitLine: {
                    lineStyle: {color: 'rgba(255,255,255,0.1)'}
                },
                axisTick: {
                    length: 0
                },
                min: function (value) {
                    let max = Math.abs(value.min) > Math.abs(value.max) ? value.min : value.max;
                    max = -Math.abs(max);
                    return Common.toFixedDecimal(max, 2);
                },
                max: function (value) {
                    let max = Math.abs(value.min) > Math.abs(value.max) ? value.min : value.max;
                    max = -Math.abs(max);
                    return Common.toFixedDecimal(max, 2);
                }
            },
            series: []
        };
        if (me.isBig) {
            option.title = {
                text: '时程曲线',
                left: 'center',
                top: '3%',
                textStyle: {
                    color: 'white'
                }
            };
            option.textStyle = {
                fontSize: '14',
                color: 'white'
            };
            option.grid = {
                left: '5%',
                right: '3%',
                bottom: '15%'
            };
        }
        return option;
    }
    update(controlParams, accs) {
        let me = this;
        let markArea = null;
        let visualMap = null;
        let data = [];
        for (let j = 0; accs && j < accs.length; j++) {
            let acc = accs[j];
            data.push([acc.monitorDate, Common.toFixedDecimal(acc.sumValue, 6)]);
        }
        if (controlParams && controlParams.length > 0) {
            let param = controlParams[0];
            let control = param.accControlValue;
            let alarm = param.accAlarmValue;
            let warn = param.accWarnValue;

            markArea = Common.getChartMarkArea(control, alarm, warn);
            visualMap = Common.getChartVisualMap(control, alarm, warn);

        } else {
            markArea = Common.getChartMarkArea(0, 0, 0);
            visualMap = Common.getChartVisualMap(0, 0, 0);
        }
        let tempSeries = [
            {
                type: 'line',
                symbolSize: 1,
                data: data,
                markArea: markArea
            }
        ];
        if (data.length <= 0) {
            tempSeries = [
                {
                    type: 'line',
                    symbolSize: 1,
                    data: Common.getXTime()
                }
            ]
        }
        let option = me.getOption();
        option.yAxis = {
            type: 'value',
            axisLine: {
                lineStyle: {color: 'rgba(255,255,255,0.2)'}
            },
            splitLine: {
                lineStyle: {color: 'rgba(255,255,255,0.1)'}
            },
            axisTick: {
                length: 0
            }
        };
        option.visualMap = visualMap;
        option.series = tempSeries;
        option.xAxis.max = function (value) {
            return Common.getMaxValue(value);
        };
        me.chart.setOption(option, true);
    }

}
