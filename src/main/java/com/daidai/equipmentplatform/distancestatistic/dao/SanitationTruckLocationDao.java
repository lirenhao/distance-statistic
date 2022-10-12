package com.daidai.equipmentplatform.distancestatistic.dao;

import com.daidai.equipmentplatform.distancestatistic.model.SanitationTruckLocation;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.stream.Stream;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午3:59
 **/
public interface SanitationTruckLocationDao extends ElasticsearchRepository<SanitationTruckLocation, String> {

    Stream<SanitationTruckLocation> findBySmsCodeAndAddTimeBetween(String smsCode, String startTime, String endTIme, Sort sort);
}