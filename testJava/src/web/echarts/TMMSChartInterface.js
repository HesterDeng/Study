import echarts from "echarts"
export default class TMMSChartInterface {
    constructor(container, isBig = false){
        if (!container) {
            throw 'container is necessary'
        }
        this.container = container;//存放这个图形的div
        this.isBig = isBig;//是否为大的图形
        this.chart = null;
        this.init();
    }
    getOption(){}
    update(){}
    addClickEvent(){}
    resize(){
        this.chart.resize();
    }
    init(){
        let me = this;
        if (me.chart) return true;
        me.chart = echarts.init(me.container);
        me.chart.setOption(me.getOption(), true);
        me.addClickEvent();
    }
}