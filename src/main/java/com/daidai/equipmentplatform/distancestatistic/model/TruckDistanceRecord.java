package com.daidai.equipmentplatform.distancestatistic.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午1:55
 * <p>
 * 车辆里程记录
 **/
@Data
@Entity
@Table(name = "TRUCK_DISTANCE_RECORD")
public class TruckDistanceRecord {

    @Id
    @GeneratedValue(generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = "uuid")
    private String id;

    /**
     * sms code
     */
    @Column(name = "SMS_CODE")
    private String smsCode;

    /**
     * 记录开始时间，包含
     */
    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    /**
     * 记录结束时间，不包含
     */
    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    /**
     * 里程，单位：cm
     */
    @Column(name = "DISTANCE")
    private Integer distance;

    /**
     * 在线时长，单位：s
     */
    @Column(name = "ONLINE_TIMES")
    private Integer onlineTimes;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
