import * as d3 from 'd3'
import common from "../common/common.js";
import DataColor from "./DataToColor";

let dateColor = new DataColor();
var protoPath = require("./MonitorPoint.json");
let protobufRoot = require('protobufjs').Root;
//以d3.js画展开图，支持缩放
export default class TMMSZhankaituDetail {
    constructor(target, gallery, callBack) {
        this.target = target;
        this.detailData = null;//断面列表数据
        this.gallery = null;
        this.callBack = callBack;
        this.svgHeight = this.target.offsetHeight;
        this.svgWidth = this.target.offsetWidth;
        this.xScale = null;
        this.yScale = null;
        this.pointList = [];
        this.startLegend = null;
        this.endLegend = null;
        this.sectionPerLegend = 0.05;
        this.perSectionNum = 0;
        d3.select(this.target).selectAll("*").remove();
    }

    async setDetailData(detailData,gallery) {
        let me = this;
        if(common.isNullEmpty(me.gallery)||gallery.id != me.gallery.id){
            d3.select(this.target).selectAll("*").remove();
            me.gallery = gallery;
        }
        me.detailData = detailData;
        if (common.isNullEmpty(me.startLegend) || common.isNullEmpty(me.endLegend) ||
            me.startLegend != me.detailData.startLegend || me.endLegend != me.detailData.endLegend) {
            me.startLegend = me.detailData.startLegend;
            me.endLegend = me.detailData.endLegend;
            me.pointList = await me.getPointData();
            me.initSvg();
        } else {
            me.initSvg();
        }

    }

    async initSvg() {
        let me = this;
        let pointList = me.pointList;
        if (!pointList || pointList.length <= 0) return;
        me.sectionPerLegend = _.subtract(pointList[0].endLegend, pointList[0].startLegend);
        me.perSectionNum = pointList[0].perSectionNum;
        //生成图片
        let minMax = [me.detailData.controlMin, me.detailData.controlMax];
        let imageList = me.canvasPic(pointList, minMax);

        //初始化d3
        let width = me.svgWidth;
        let height = me.svgHeight;
        d3.select(me.target).selectAll("*").remove();
        let svg = d3.select(me.target).append("svg").attr("width", me.svgWidth).attr("height", me.svgHeight);

        let sectionLegend = _.multiply(me.perSectionNum, me.sectionPerLegend);
        me.yScale = d3.scaleLinear()
            .domain([_.divide(sectionLegend, 2), -_.divide(sectionLegend, 2)])
            .range([0, height]);
        let legendEnd = sectionLegend / height * width;
        me.xScale = d3.scaleLinear()
            .domain([me.detailData.startLegend, me.detailData.startLegend + legendEnd])
            .range([0, width]);

        let content = svg.append("g").attr("class", "main-chart").attr("width", width).attr("height", height);
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
            .data(imageList)
            .enter()
            .append('g')
            .attr('clip-path', 'url(#clip-main)');
        rectPic
            .append('image')
            .attr("transform", function (item) {
                let scale = _.divide(height, me.perSectionNum);
                let x = me.xScale(item.minLegend);
                let y = me.yScale(_.divide(sectionLegend, 2));
                return "translate(" + x + "," + y + ")scale(" + scale + ")";
            })
            .attr("xlink:href", function (item) {
                return item.imageURL;
            });
        let xScale = me.xScale;
        let yScale = me.yScale;

        //缩放函数
        function zoomed() {
            if (d3.event.sourceEvent && d3.event.sourceEvent.type === 'brush') return;
            let t = d3.event.transform;
            yScale = t.rescaleY(me.yScale);
            xScale = t.rescaleX(me.xScale);
            rectPic
                .select('image')
                .attr("transform", function (item) {
                    let originScale = _.divide(height, me.perSectionNum);
                    let scale = t.k;
                    let x = xScale(item.minLegend);
                    let y = yScale(_.divide(sectionLegend, 2));
                    let tempScale = _.multiply(originScale, scale);
                    return "translate(" + x + "," + y + ")scale(" + tempScale + ")";
                    // return t;
                });
        }

        //定义缩放zoom
        var zoom = d3
            .zoom()
            .scaleExtent([0, Infinity])
            .on('zoom', zoomed);
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
                let legend = xScale.invert(d3.event.offsetX);
                let yIndex = 0;
                let position = _.multiply(yScale.invert(d3.event.offsetY), _.divide(1, me.sectionPerLegend));
                if (position > 0) {
                    yIndex = me.perSectionNum - position;
                } else {
                    yIndex = Math.abs(position);
                }
                if (me.callBack) {
                    me.callBack(legend, yIndex);
                }
            })


    }

    canvasPic(pointList, minMax) {
        let me = this;
        let returnData = [];
        returnData[0] = {};
        let minLegend = d3.min(pointList, function (d) {
            return d.startLegend;
        });
        returnData[0].minLegend = minLegend;
        let maxLegend = d3.max(pointList, function (d) {
            return d.endLegend;
        });
        returnData[0].maxLegend = maxLegend;
        returnData[0].perSectionNum = d3.max(pointList, function (d) {
            return d.perSectionNum;
        });
        let canvasWidth = Math.round(_.divide(_.subtract(maxLegend, minLegend), me.sectionPerLegend));
        let canvasHeight = returnData[0].perSectionNum;
        let canvas = document.createElement('canvas');
        canvas.width = canvasWidth;
        canvas.height = canvasHeight;
        let ctx = canvas.getContext("2d");
        let image = ctx.getImageData(0, 0, canvasWidth, canvasHeight);
        let imageData = image.data;
        let category = parseInt(me.detailData.category);
        for (let i = 0; i < pointList.length; i++) {
            let section = pointList[i];
            let sectionNum = Math.round(_.divide(_.subtract(section.startLegend, minLegend), me.sectionPerLegend));
            let midSectionNum = Math.round(section.perSectionNum/2)-1;
            for (let j = 0; j < section.perSectionNum; j++) {
                let color = null;
                if (category === 1) {
                    color = dateColor.dataToColor2(section.cjSum[j], minMax);
                } else if (category === 2) {
                    color = dateColor.dataToColor2(section.cjSpeed[j], minMax);
                } else if (category === 3) {
                    color = dateColor.dataToColor2(section.slSum[j], minMax);
                } else {
                    color = dateColor.dataToColor2(section.slSpeed[j], minMax);
                }
                let index = 0;
                if(j<midSectionNum){
                    index = ((j+midSectionNum) * canvasWidth + sectionNum) * 4;
                }else{
                    index = ((j-midSectionNum) * canvasWidth + sectionNum) * 4;
                }
                imageData[index] = color[0];
                imageData[index + 1] = color[1];
                imageData[index + 2] = color[2];
                imageData[index + 3] = color[3];
            }
        }
        ctx.putImageData(image, 0, 0);
        let imageURL = canvas.toDataURL("image/png");
        returnData[0].imageURL = imageURL;
        return returnData;
    }

    async getPointData() {
        let me = this;
        let params = {
            foreignId: me.gallery.id,
            type: "2",
            startMile: me.detailData.startLegend,
            endMile: me.detailData.endLegend
        };
        let resp = await axios.get("/tmms/points/pcasZip", {
            params: params,
            responseType: 'blob'
        });

        let data = resp.data;
        if (data.type === 'application/zip') {
            let zip = await window.JSZip.loadAsync(resp.data);
            let files = Object.keys(zip.files);
            if (files.length > 0) {
                let value = files[0];
                let points = await zip.file(value).async("arraybuffer");
                let root = protobufRoot.fromJSON(protoPath);
                let MonitorPointList = root.lookupType('MonitorPointList');
                let array = new Uint8Array(points);
                let result = MonitorPointList.decode(array);
                return result.pointList;
            }

        }
    }

}
