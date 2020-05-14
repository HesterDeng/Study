export default class TMMSChartCommon{

    static toFixedDecimal(num, range) {
        if (isNaN(num)) return num;
        if (range < 0) return num;
        let newNum = num.toFixed(range);
        let reg1 = /\d+\.\d+0+$/; //匹配 xxx.xxx000这样的情况
        if (reg1.test(newNum)) {
            let i = newNum.length - 1;
            while (i >= 0 && newNum.charAt(i) === '0') {
                newNum = newNum.substring(0, i);
                i--;
            }
            return parseFloat(newNum);
        }

        let reg2 = /\d+\.0+$/; //匹配 xxx.00000这样的情况
        if (reg2.test(newNum)) {
            return parseFloat(newNum.replace(/\.0+$/, '.0'));
        }
        return parseFloat(newNum);
    }

    static getMaxValue(value) {
        let max = Number.isFinite(value.max) ? value.max : Math.ceil(value.max / (1000 * 60 * 60 * 24)) * (1000 * 60 * 60 * 24);
        // max = max - (1000 * 60 * 60 * 8);
        return max;
    };

    //获取时间段的坐标
    static getXTime() {
        let currentTime = new Date().getTime();
        let xTimes = [];
        for (let i = 0; i < 5; i++) {
            let temp = new Date(currentTime + (i * 24 * 60 * 60 * 1000));
            temp.setMinutes(0);
            temp.setHours(0);
            temp.setSeconds(0);
            xTimes.push([temp, 0]);
        }
        return xTimes;
    };

    //获取控制基准背景颜色
    static getChartMarkArea(control, alarm, warn) {
        let markArea = {
            silent: true,
            label: {
                show: true,
                position: 'inside',
                fontSize: 10
            },
            data: []
        };

        if (control && control > 0) {
            markArea.data.push([
                {
                    yAxis: control,
                    itemStyle: {
                        color: '#ff6f80',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: 99999
                }
            ], [
                {
                    yAxis: -99999,
                    itemStyle: {
                        color: '#ff6f80',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: -control
                }
            ]);
        }

        if (alarm && alarm > 0) {
            let tempControl1 = control > 0 ? control : 9999;
            markArea.data.push([
                {
                    yAxis: alarm,
                    itemStyle: {
                        color: '#ff9537',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: tempControl1
                }
            ], [
                {
                    yAxis: -tempControl1,
                    itemStyle: {
                        color: '#ff9537',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: -alarm
                }
            ]);
        }

        if (warn && warn > 0) {
            let tempAlarm = alarm > 0 ? alarm : control > 0 ? control : 9999;//设置了橙色预警，也可能没有
            markArea.data.push([
                {
                    yAxis: warn,
                    itemStyle: {
                        color: '#ffc838',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: tempAlarm
                }
            ], [
                {
                    yAxis: -tempAlarm,
                    itemStyle: {
                        color: '#ffc838',
                        opacity: 0.1
                    }
                },
                {
                    yAxis: -warn
                }
            ]);
        }
        let greenValue = warn > 0 ? warn : alarm > 0 ? alarm : control > 0 ? control : 9999;
        markArea.data.push([
            {
                yAxis: -greenValue,
                itemStyle: {
                    color: '#52ff5a',
                    opacity: 0.1
                }
            },
            {
                yAxis: greenValue
            }
        ]);
        return markArea;
    };

    //获取控制基准线条颜色
    static getChartVisualMap(control, alarm, warn) {
        let visualMap = {
            type: 'piecewise',
            show: false,
            pieces: []
        };
        //红色预警
        if (control > 0){
            visualMap.pieces.push({
                gte: control,
                color: '#ff6f80'
            });
            visualMap.pieces.push({
                lte: -control,
                color: '#ff6f80'
            });
        }

        //橙色预警
        if (alarm > 0){
            let tempControl = control > 0 ? control : 9999;
            visualMap.pieces.push({
                gte: alarm,
                lt: tempControl,
                color: '#ff9537'
            });
            visualMap.pieces.push({
                gt: -tempControl,
                lte: -alarm,
                color: '#ff9537'
            });
        }

        //黄色预警
        if (warn > 0){
            let tempAlarm = alarm > 0 ? alarm : control > 0 ? control : 9999;
            visualMap.pieces.push({
                gte: warn,
                lt: tempAlarm,
                color: '#ffc838'
            });
            visualMap.pieces.push({
                gt: -tempAlarm,
                lte: -warn,
                color: '#ffc838'
            });
        }
        //绿色
        let greenValue = warn > 0 ? warn : alarm > 0 ? alarm : control > 0 ? control : 9999;
        visualMap.pieces.push({
            gt: -greenValue,
            lt: greenValue,
            color: '#52ff5a'
        });
        return visualMap;
    }

    static formatLegend(number) {
        let num = parseFloat(number);
        let t = num / 1000;
        let nt = num % 1000;
        if (nt < 100 && nt >= 10) {
            nt = '0' + Math.floor(nt);
        } else if (nt < 10 && nt >= 0) {
            nt = '00' + Math.floor(nt);
        } else {
            nt = Math.floor(nt);
        }
        return Math.floor(t) + '+' + nt;
    }

    static formatLegendWithPrefix(prefix, number) {
        return prefix + TMMSChartCommon.formatLegend(number);
    }
}
