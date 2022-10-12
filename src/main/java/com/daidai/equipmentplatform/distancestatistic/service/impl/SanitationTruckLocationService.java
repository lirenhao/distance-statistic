package com.daidai.equipmentplatform.distancestatistic.service.impl;

import com.daidai.equipmentplatform.distancestatistic.constant.CommonConstants;
import com.daidai.equipmentplatform.distancestatistic.dao.SanitationTruckLocationDao;
import com.daidai.equipmentplatform.distancestatistic.model.SanitationTruckLocation;
import com.daidai.equipmentplatform.distancestatistic.service.ISanitationTruckLocationService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchScrollHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 获取ES中的坐标数据
 *
 * @Auther lirenhao
 * @Date 2022/10/11 下午2:21
 **/
@Service
public class SanitationTruckLocationService implements ISanitationTruckLocationService {

    @Resource
    private ElasticsearchRestTemplate template;
    @Resource
    private SanitationTruckLocationDao sanitationTruckLocationDao;

    /**
     * spring的Repository不支持聚合
     * ElasticsearchRestTemplate的Aggregations需要自己解析转换
     * smsCode的类型不能为text,否则不支持聚合
     */
    @Override
    public List<String> getSmsCodes(String startTime, String endTime) {
        IndexCoordinates index = IndexCoordinates.of(CommonConstants.SANITATION_TRUCK_LOCATION_INDEX);
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery(CommonConstants.ADD_TIME_FIELD).gte(startTime).lte(endTime))
                .withAggregations(AggregationBuilders.terms(CommonConstants.SMS_CODE__AGG).field(CommonConstants.SMS_CODE_FIELD))
                .withPageable(PageRequest.of(0, 1))
                .build();
        SearchHits<SanitationTruckLocation> hits =  template.search(query, SanitationTruckLocation.class, index);
        if(hits.hasAggregations()) {
            Map<String, Long> smsCodes = getSmsCodes((Aggregations) hits.getAggregations().aggregations());
            return new ArrayList<>(smsCodes.keySet());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * spring没有提供聚合的解析起，需要自定义解析
     */
    private Map<String, Long> getSmsCodes(Aggregations aggregations) {
        if (aggregations != null) {
            Aggregation smsCodes = aggregations.get(CommonConstants.SMS_CODE__AGG);
            if (smsCodes != null) {
                List<? extends Terms.Bucket> buckets = ((Terms) smsCodes).getBuckets();
                if (buckets != null) {
                    return buckets.stream().collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount));
                }
            }
        }
        return Collections.emptyMap();
    }

    /**
     * 通过template自定义scroll循环获取数据
     * addTime的类型不能为text,否则不支持排序
     */
    @Override
    public List<SanitationTruckLocation> getList(String smsCode, String startTime, String endTime) {
        IndexCoordinates index = IndexCoordinates.of(CommonConstants.SANITATION_TRUCK_LOCATION_INDEX);
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(CommonConstants.SMS_CODE_FIELD, smsCode))
                .withQuery(QueryBuilders.rangeQuery(CommonConstants.ADD_TIME_FIELD).gte(startTime).lt(endTime))
                .withPageable(PageRequest.of(0, 2000))
                .build();

        SearchScrollHits<SanitationTruckLocation> scroll = template
                .searchScrollStart(10000, query, SanitationTruckLocation.class, index);

        String scrollId;
        List<SanitationTruckLocation> result = new ArrayList<>();
        List<String> scrollIds = new ArrayList<>();
        while (scroll.hasSearchHits()) {
            result.addAll(scroll.get().map(SearchHit::getContent).collect(Collectors.toList()));
            scrollId = scroll.getScrollId();
            scrollIds.add(scrollId);
            scroll = template.searchScrollContinue(scrollId, 10000, SanitationTruckLocation.class, index);
        }
        template.searchScrollClear(scrollIds);
        return result;
    }

    /**
     * 通过spring的Repository获取数据
     * 返回Stream时spring默认使用scroll
     * addTime的类型不能为text,否则不支持排序
     */
    @Override
    public Stream<SanitationTruckLocation> getStream(String smsCode, String startTime, String endTime) {
        return sanitationTruckLocationDao.findBySmsCodeAndAddTimeBetween(
                smsCode, smsCode, endTime, Sort.by(CommonConstants.ADD_TIME_FIELD).ascending());
    }

}
