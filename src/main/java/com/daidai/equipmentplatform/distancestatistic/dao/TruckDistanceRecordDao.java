package com.daidai.equipmentplatform.distancestatistic.dao;

import com.daidai.equipmentplatform.distancestatistic.model.TruckDistanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午3:59
 **/
public interface TruckDistanceRecordDao extends JpaRepository<TruckDistanceRecord, String>, JpaSpecificationExecutor<TruckDistanceRecord> {

    long deleteBySmsCodeAndStartTimeBetween(String smsCode, LocalDateTime startTime, LocalDateTime endTime);
}