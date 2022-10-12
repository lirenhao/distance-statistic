package com.daidai.equipmentplatform.distancestatistic.service;

import com.daidai.equipmentplatform.distancestatistic.model.SanitationTruckLocation;

import java.util.List;
import java.util.stream.Stream;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午2:13
 **/
public interface ISanitationTruckLocationService {

    /**
     * 获取时间段内所有对smsCode
     *
     * @param startTime 开始时间，格式：yyyy-MM-dd HH:mm:ss
     * @param endTime 结束时间，格式：yyyy-MM-dd HH:mm:ss
     * @return smsCode列表
     */
    List<String> getSmsCodes(String startTime, String endTime);

    /**
     * 自定义实现获取数据
     *
     * @param smsCode smsCode
     * @param startTime  startTime 开始时间，格式：yyyy-MM-dd HH:mm:ss
     * @param endTime endTime 结束时间，格式：yyyy-MM-dd HH:mm:ss
     * @return smsCode时间段内的数据
     */
    List<SanitationTruckLocation> getList(String smsCode, String startTime, String endTime);

    /**
     * 通过spring jpa获取数据
     *
     * @param smsCode smsCode
     * @param startTime  startTime 开始时间，格式：yyyy-MM-dd HH:mm:ss
     * @param endTime endTime 结束时间，格式：yyyy-MM-dd HH:mm:ss
     * @return smsCode时间段内的数据
     */
    Stream<SanitationTruckLocation> getStream(String smsCode, String startTime, String endTime);
}
