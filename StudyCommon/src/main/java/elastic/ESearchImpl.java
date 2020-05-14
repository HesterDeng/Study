package elastic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tunnelkey.tktim.infrastructure.StringExtension;
import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.elastic.ESearchRequest;
import com.tunnelkey.tktim.model.elastic.ESearchResponse;
import com.tunnelkey.tktim.model.elastic.TKTIMIndexEnum;
import com.tunnelkey.tktim.model.elastic.TKTIMIndexType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version:（1.0.0.0）
 * @Description: 检索统一查询接口实现
 * @author: enoch
 * @date: 2018/12/19 16:00
 */
@Service
@Slf4j(topic = "com.tunnelkey.tktim.api.Application")
public class ESearchImpl implements ESearchService {

    @Autowired
    private TransportClient client;
    @Override
    public PageModel<ESearchResponse> search(ESearchRequest request) {
        PageModel<ESearchResponse> items = new PageModel<>();
        items.DataList = new ArrayList<>();
        try {
            long pageNum = request.getPageable().PageNum;
            long pageSize = request.getPageable().PageSize;
            items.PageNum = pageNum;
            items.PageSize = pageSize;

            //条件查询should=or; must=and
            /**
             * 组合查询
             * must(QueryBuilders) :   AND
             * mustNot(QueryBuilders): NOT
             * should:                  : OR
             */
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(request.startLegend) && !StringUtils.isEmpty(request.endLegend)) {
                RangeQueryBuilder legendq = QueryBuilders.rangeQuery("startLegend");
                legendq.gte(Float.parseFloat(request.startLegend));
                legendq.lte(Float.parseFloat(request.endLegend));
                RangeQueryBuilder legendeq = QueryBuilders.rangeQuery("endLegend");
                legendeq.gte(Float.parseFloat(request.startLegend));
                legendeq.lte(Float.parseFloat(request.endLegend));
                boolQueryBuilder.must(legendq).must(legendeq);
            }
            if (!ObjectUtils.isEmpty(request.startTime) && !ObjectUtils.isEmpty(request.endTime)) {
                RangeQueryBuilder legendq = QueryBuilders.rangeQuery("createTime.keyword");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                legendq.gte(formatter.format(request.startTime));
                legendq.lte(formatter.format(request.endTime));
                boolQueryBuilder.must(legendq);
            }
            //企业权限匹配
            if (!ObjectUtils.isEmpty(request.getTunnelIds())) {
                //在字段存在的情况下，查询必须满足隧道字段的集合数
                BoolQueryBuilder tidQuery = new BoolQueryBuilder();
                tidQuery.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("tunnelId")));//要么不存在这个字段
                List<String> collect = request.getTunnelIds().stream().map(it -> String.valueOf(it)).collect(Collectors.toList());
                //默认是text类型不支持聚合和排序，所以用tunnelId.keyword
                TermsQueryBuilder tunnelId = QueryBuilders.termsQuery("tunnelId.keyword", collect);// in:要么就必须在这个隧道集中中
                tidQuery.should(tunnelId);//要么必须在这个隧道集合中

                boolQueryBuilder.must(tidQuery);
            }
            if (!request.isLogin) {
                BoolQueryBuilder tidQuery = new BoolQueryBuilder();
                tidQuery.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("tunnelId")));//未登陆只能查看帮助系统
                boolQueryBuilder.must(tidQuery);
            }
            //高亮显示设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<h2 sytle='color:red'>");
            highlightBuilder.postTags("</h2>");
            highlightBuilder.field("*").requireFieldMatch(false);//不需要配置字段也可以高亮
            QueryStringQueryBuilder buider = new QueryStringQueryBuilder(request.getKeywords()).defaultOperator(Operator.AND);
            boolQueryBuilder.must(buider);
            long skip = (pageNum - 1) * pageSize;
            SearchResponse response = client.prepareSearch()
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setExplain(true)
                    .highlighter(highlightBuilder)
                    .setFrom((int) skip).setSize((int) pageSize).setExplain(true)
                    .get();
            items.setRecordCount(response.getHits().totalHits);
            SearchHits hits = response.getHits();
            hits.forEach(it -> {
                String type = it.getType();
                String nameByValue = TKTIMIndexEnum.getNameByValue(type);
                try {
                    ESearchResponse item = new ESearchResponse();
                    item.setTypeName(nameByValue);
                    item.setType(type);
                    JSONObject jsonObject = JSON.parseObject(it.getSourceAsString());
                    String content = StringExtension.addRedColor(jsonObject.getString("content"), request.keywords);
                    item.setContent(content);
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime createTime = LocalDateTime.parse(jsonObject.get("createTime").toString(), df);
                    item.setUserName(jsonObject.getString("createUserName"));
                    item.setUpdateAgo(updateDateStr(createTime));
                    item.setCreateTime(createTime);
                    item.setKeyId(jsonObject.getString("keyId"));
                    items.DataList.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("-------------查询结果------------" + it.getSourceAsString());
                System.out.println("-------------高亮结果------------" + it.getHighlightFields());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private String updateDateStr(LocalDateTime createDate) {
        String tip = "";
        if (createDate == null) {
            return tip;
        }
        LocalDateTime now = LocalDateTime.now();
        Duration between = Duration.between(createDate, now);
        long hs = between.toMillis();
        if (hs >= 0 && hs < 1000) {
            tip = hs + "毫秒前更新";
        }
        if (StringUtils.isEmpty(tip)) {
            long ss = hs / 1000L;
            if (ss >= 0 && ss <= 60) {
                tip = ss + "秒钟前更新";
            }
        }

        if (StringUtils.isEmpty(tip)) {
            long fz = between.toMinutes();   //两个时间差的分钟数
            if (fz >= 0 && fz <= 60) {
                tip = fz + "分钟前更新";
            }
        }
        if (StringUtils.isEmpty(tip)) {
            long xs = between.toHours();
            if (xs >= 0 && xs <= 24) {
                tip = xs + "小时前更新";
            }
        }
        if (StringUtils.isEmpty(tip)) {
            long t = between.toDays();
            if (t >= 0 && t <= 3) {
                tip = t + "天前更新";
            }
        }
        return tip;
    }


    public List<String> getIndexes() {
        List<String> pers = new ArrayList<>();
        TKTIMIndexType obj = new TKTIMIndexType();
        Class<? extends TKTIMIndexType> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(field -> {
            try {
                Object o = field.get(obj);
                pers.add(o.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return pers;
    }
//
//    @Override
//    public void init() {
//
//        wallrock();
//        risk();
//        construct();
//        agf();
//        buliang();
//        face();
//        drill();
//        wutan();
//        zonghe();
//    }
//
//    /**
//     * 建立围岩的默认索引
//     */
//    private void wallrock() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, WallRockClassification.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("围岩需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<WallRockClassification> sections = mongoTemplate.find(query, WallRockClassification.class);
//            sections.forEach(it -> {
//                indexWallRockService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("围岩创建完成");
//    }
//
//    /**
//     * 建立风险的默认索引
//     */
//    private void risk() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, GeologyRiskAssessment.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("风险需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<GeologyRiskAssessment> sections = mongoTemplate.find(query, GeologyRiskAssessment.class);
//            sections.forEach(it -> {
//                indexRiskService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("风险创建完成");
//    }
//
//    /**
//     * 建立施工方方达的默认索引
//     */
//    private void construct() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, ConstructMethod.class);
//        log.warn("施工方法需要创建的条数总计【" + count + "】");
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<ConstructMethod> sections = mongoTemplate.find(query, ConstructMethod.class);
//            sections.forEach(it -> {
//                indexConstructMethodService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("施工方法创建完成");
//    }
//
//    /**
//     * 建立不良地质的默认索引
//     */
//    private void buliang() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, UnfavorableGeologyModel.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("不良地质需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<UnfavorableGeologyModel> sections = mongoTemplate.find(query, UnfavorableGeologyModel.class);
//            sections.forEach(it -> {
//                indexUnfavorableService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("不良地质创建完成");
//    }
//
//    /**
//     * 建立设计预报的默认索引
//     */
//    private void agf() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, AdvanceGeologyForecast.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("地质预报需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<AdvanceGeologyForecast> sections = mongoTemplate.find(query, AdvanceGeologyForecast.class);
//            sections.forEach(it -> {
//                indexAGFService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("地质预报创建完成");
//    }
//
//    /**
//     * 素描索引
//     */
//    private void face() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, TunnelFaceNewModel.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("素描需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<TunnelFaceNewModel> sections = mongoTemplate.find(query, TunnelFaceNewModel.class);
//            sections.forEach(it -> {
//                indexFaceService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("素描创建完成");
//    }
//
//    /**
//     * 钻探索引
//     */
//    private void drill() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, GeologyDrillModel.class);
//        log.warn("钻探需要创建的条数总计【" + count + "】");
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<GeologyDrillModel> sections = mongoTemplate.find(query, GeologyDrillModel.class);
//            sections.forEach(it -> {
//                indexDrillService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("钻探创建完成");
//    }
//
//    /**
//     * 物探索引
//     */
//    private void wutan() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, GeophysicalAndReportModel.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("物探需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<GeophysicalAndReportModel> sections = mongoTemplate.find(query, GeophysicalAndReportModel.class);
//            sections.forEach(it -> {
//                indexGeophysicalService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("物探创建完成");
//    }
//
//    /**
//     * 综合预报
//     */
//    private void zonghe() {
//        Query query = Query.query(Criteria.where("_id").exists(true).and("CreateTime").gte(LocalDateTime.of(2019, 1, 1, 0, 0, 0)));
//        int pageSize = 10;
//        long count = mongoTemplate.count(query, AGFComprehensiveModel.class);
//        int pageCount = (int) Math.ceil(((double) count) / pageSize);
//        log.warn("综合预报需要创建的条数总计【" + count + "】");
//        for (int i = 1; i <= pageCount; i++) {
//            query.skip((i - 1) * pageSize);
//            query.limit((int) pageSize);
////            query.with(new Sort(Sort.Direction.DESC, "CreateTime"));
//            List<AGFComprehensiveModel> sections = mongoTemplate.find(query, AGFComprehensiveModel.class);
//            sections.forEach(it -> {
//                indexComprehensiveService.save(it);
//            });
//            log.warn("新增【" + sections.size() + "】");
//        }
//        log.warn("综合预报创建完成");
//    }
}
