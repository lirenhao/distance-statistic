package com.daidai.equipmentplatform.distancestatistic.schedule;

import cn.hutool.core.date.DateUtil;
import com.daidai.equipmentplatform.distancestatistic.service.ITruckDistanceRecordService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 车辆里程计划任务
 *
 * @Auther lirenhao
 * @Date 2022/10/12 下午4:14
 **/
@Component
@Slf4j
public class TruckDistanceSchedule {

    @Resource
    private ITruckDistanceRecordService truckDistanceRecordService;

    /**
     * 计算车辆里程在线时长
     */
    @XxlJob("truckDistanceRecordHandler")
    public void truckDistanceRecordHandler(){
        log.info("truckDistanceRecordHandler-start");
        String date = DateUtil.offsetDay(new Date(), -1).toString("yyyy-MM-dd");
        truckDistanceRecordService.handleAllData(date);
        log.info("truckDistanceRecordHandler-end");
    }

}
