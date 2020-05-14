
//以d3.js画展开图，支持缩放
export default class TMMSZhankaituSvg {
    constructor(target, gallery, sectionData, callBack) {
        this.target = target;
        this.sectionData = sectionData;//断面列表数据
        this.gallery = gallery;
        this.galleryStartLegend = gallery.startLegend;
        this.galleryEndLegend = gallery.endLegend;
        this.legendPrefix = gallery.prefix;
        this.tunnelLength = this.galleryEndLegend - this.galleryStartLegend;//隧道总长度 单位： m（米）
        this.callBack = callBack;

        this.svgHeight = this.target.offsetHeight;
        this.svgWidth = this.target.offsetWidth;
        this.xScale = null;
        this.yScale = null;

        //最小高度
        this.sectionHeight = 452;
        this.sectionPerLegend = sectionData&&sectionData.size>0?0.05:sectionData[0].perSectionLegendLength;

        this.initSvg();
    }

    initSvg() {
        let me = this;
        let brushHeight = 40;
        d3.select(me.target).selectAll("*").remove();
        let svg = d3.select(me.target).append("svg").attr("width", me.svgWidth).attr("height", me.svgHeight);

        let margin = {top: 0, right: 40, bottom: 20 + brushHeight, left: 40};
        let margin2 = {top: me.svgHeight - brushHeight, right: 40, bottom: 20, left: 40};
        let width = me.svgWidth - margin.left - margin.right;
        let height = me.svgHeight - margin.top - margin.bottom;
        let height2 = me.svgHeight - margin2.top - margin2.bottom;
        let width2 = me.svgWidth - margin2.left - margin2.right;

        me.sectionHeight = (me.galleryEndLegend - me.galleryStartLegend) / width * height;

        //scale
        me.xScale = d3.scaleLinear()
            .domain([this.galleryStartLegend, this.galleryEndLegend])
            .range([0, width]);

        let xScale2 = d3.scaleLinear().domain(me.xScale.domain()).range([0, width2]);

        me.yScale = d3.scaleLinear()
            .domain([0, me.sectionHeight])
            .range([0, height]);


        //x轴主轴
        let xAxis = d3.axisBottom(me.xScale).tickFormat(function (d) {
            let legend = common.formatLegendPrefix(me.legendPrefix, d);
            return legend;
        }).ticks(10);
        //x轴副轴
        let xAxis2 = d3.axisBottom().scale(xScale2).tickFormat(function (d) {
            let legend = common.formatLegendPrefix(me.legendPrefix, d);
            return legend;
        }).ticks(10);
        //y轴主轴
        let yAxis = d3.axisLeft(me.yScale).ticks(10);

        let content = svg.append("g").attr("class", "main-chart").attr('transform', 'translate(' + margin.left + ',' + margin.top + ')').attr("width", width).attr("height", height);
        content.append("g").attr("class", "axis--x").attr('transform', 'translate(' + 0 + ',' + (margin2.top - 20) + ')').call(xAxis);
        content.append("g").attr("class", "axis--y").attr('transform', 'translate(' + 0 + ',' + margin.top + ')').call(yAxis);

        content
            .append('defs')
            .append('clipPath')
            .attr('id', 'clip-main')
            .append('rect')
            .attr('height', height)
            .transition()
            .duration(2000)
            .attr('width', width);

        let rectPic = content
            .append('g')
            .selectAll('g')
            .data(me.sectionData)
            .enter()
            .append('g')
            .attr('clip-path', 'url(#clip-main)');
        rectPic
            .append('image')
            .attr("legend", function (item) {
                return common.formatRangeLegend(me.legendPrefix, item.startLegend, item.endLegend);
            })
            .attr("transform", function (item) {
                let scale = (me.xScale(item.endLegend) - me.xScale(item.startLegend)) / ((item.endLegend - item.startLegend) / item.perSectionLegendLength);
                let x = me.xScale(item.startLegend);
                return "translate(" + x + ",0)scale(" + scale + ")";
            })
            .attr("xlink:href", function (item) {
                let image = item.viewer ? item.viewer.imgFile : null;
                if (image) {
                    return common.getBase64Image(image);
                }
            })


        //brush区域:动态比例尺
        let subChart = svg.append("g").attr('transform', 'translate(' + margin2.left + ',' + margin2.top + ')');
        //添加x轴到页面
        subChart
            .append('g')
            .attr('transform', 'translate( 0, ' + height2 + ' )')
            .call(xAxis2)

        let brushBox = subChart.append("rect").attr("width", width2).attr("height", height2).style("fill", "#FFFFFF").style("fill-opacity", "0.5");

        //画刷函数
        function brushed() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === 'zoom') return
            let s = d3.event.selection || xScale2.range()
            me.xScale.domain(s.map(xScale2.invert, xScale2))
            limitxScale(me.xScale);
            content.select('.axis--x').call(xAxis.scale(me.xScale))
            let sectionHeight = (me.xScale.domain()[1] - me.xScale.domain()[0]) / width * height;
            let start = me.yScale.domain()[0];
            me.yScale.domain([start, start+sectionHeight]);
            content.select('.axis--y').call(yAxis.scale(me.yScale))
            rectPic
                .select('image')
                .attr("transform", function (item) {
                    let originScale = (xScale2(item.endLegend) - xScale2(item.startLegend)) / ((item.endLegend - item.startLegend) / item.perSectionLegendLength);
                    let scale = (me.xScale(item.endLegend) - me.xScale(item.startLegend)) / (xScale2(item.endLegend) - xScale2(item.startLegend));
                    let x = me.xScale(item.startLegend);
                    let y = me.yScale(0);
                    return "translate(" + x + "," + y + ")scale(" + originScale * scale + ")";
                });

        }

        function limitxScale(xScale) {
            let domain = xScale.domain();
            if (domain[0] < xScale2.domain()[0]) {
                domain[0] = xScale2.domain()[0];
            }
            if (domain[1] > xScale2.domain()[1]) {
                domain[1] = xScale2.domain()[1];
            }
            if (domain[1] - domain[0] < 5) {
                domain[1] = domain[0] + 5;
            }
            xScale.domain(domain);
        }

        //缩放函数
        function zoomed() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === 'brush') return;
            let t = d3.event.transform
            let xScale = t.rescaleX(me.xScale);
            let yScale = t.rescaleY(me.yScale);
            limitxScale(xScale);
            content.select('.axis--x').call(xAxis.scale(xScale));

            // let domain = xScale.domain();
            // let sectionHeight = (domain[1] - domain[0]) / width * height;
            // yScale.domain([0, sectionHeight]);
            content.select('.axis--y').call(yAxis.scale(yScale));
            rectPic
                .select('image')
                .attr("transform", function (item) {
                    let originScale = (xScale2(item.endLegend) - xScale2(item.startLegend)) / ((item.endLegend - item.startLegend) / item.perSectionLegendLength);
                    let scale = (xScale(item.endLegend) - xScale(item.startLegend)) / (xScale2(item.endLegend) - xScale2(item.startLegend));
                    let x = xScale(item.startLegend);
                    let y = yScale(0);
                    return "translate(" + x + "," + y + ")scale(" + originScale * scale + ")";
                });
            let s = xScale.domain();
            let d = s.map(item => {
                return xScale2(item)
            });
            brush.move(subChart.select('.brush'), d);
        }


        //定义画刷
        var brush = d3
            .brushX()
            .extent([[0, 0], [width, height2]])
            .on('brush', brushed)

        //定义缩放zoom
        var zoom = d3
            .zoom() // 设置zoom
            //设置缩放范围
            .scaleExtent([0, Infinity])
            //设置transform的范围
            // .translateExtent([[0, 0], [width, height]])
            //设置缩放的视口的大小; 注:此时视口大小与transform范围一样说明无法拖动只可滚轮缩放
            // .extent([[0, 0], [width, height]])
            .on('zoom', zoomed)

        //子图
        subChart
            .append('g') // 添加画刷
            .attr('class', 'brush')
            .call(brush)
            // .call(brush.move, xScale.range())
            //初始笔刷刷取范围
            .call(brush.move, [me.xScale(100), me.xScale(200)])

        content
            .append('g')
            .append('rect') // 添加刷放方块
            .attr('class', 'zoom')
            .attr("cursor", "pointer")
            .attr('width', width)
            .attr('height', height)
            .attr('pointer-events', 'all')
            .attr('fill', 'none')
            .call(zoom)
            .on("dblclick.zoom", function () {
                let legend = xAxis.scale().invert(d3.event.offsetX - margin.left);
                let yIndex = yAxis.scale().invert(d3.event.offsetY)*(1/me.sectionPerLegend);
                if (me.callBack) {
                    me.callBack(legend, yIndex);
                }
            })

    }


}
