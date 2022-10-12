package com.daidai.equipmentplatform.distancestatistic.service;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午2:13
 **/
public interface ITruckDistanceRecordService {

    void handleAllData(String date);

    void handleData(String smsCode, String date);
}
