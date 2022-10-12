package com.daidai.equipmentplatform.distancestatistic.service.impl;

import com.daidai.equipmentplatform.distancestatistic.dao.TruckDistanceRecordDao;
import com.daidai.equipmentplatform.distancestatistic.model.SanitationTruckLocation;
import com.daidai.equipmentplatform.distancestatistic.model.TruckDistanceRecord;
import com.daidai.equipmentplatform.distancestatistic.service.ISanitationTruckLocationService;
import com.daidai.equipmentplatform.distancestatistic.service.ITruckDistanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午2:21
 **/
@Service
@Slf4j
public class TruckDistanceRecordService implements ITruckDistanceRecordService {

    @Resource
    private ISanitationTruckLocationService sanitationTruckLocationService;
    @Resource
    private TruckDistanceRecordDao truckDistanceRecordDao;

    @Value("${intervalTimes:600}")
    private int intervalTimes;

    private final static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 计算在${date}日期内的行驶记录
     *
     * @param date 日期，格式：yyyy-MM-dd
     */
    @Override
    public void handleAllData(String date) {
        // 获取所有的smsCode
        List<String> smsCodes = sanitationTruckLocationService
                .getSmsCodes(String.format("%s 00:00:00", date), String.format("%s 23:59:59", date));
        smsCodes.forEach(smsCode -> handleData(smsCode, date));
    }

    /**
     * 计算${smsCode}在${date}内的行驶记录
     *
     * @param smsCode SMS编号
     * @param date    日期，格式：yyyy-MM-dd
     */
    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void handleData(String smsCode, String date) {
        log.info("计算[{}]-[{}]的行驶记录开始", smsCode, date);

        // 格式化开始、结束时间
        LocalDateTime startTime = LocalDateTime.parse(String.format("%s 00:00:00", date), df);
        LocalDateTime endTime = LocalDateTime.parse(String.format("%s 23:59:59", date), df);

        // 删除之前的记录
        truckDistanceRecordDao.deleteBySmsCodeAndStartTimeBetween(smsCode, startTime, endTime);

        // 时间分片,通过配置文件的配置决定间隔时间，单位：秒
        LocalDateTime nextTime = startTime;
        while (nextTime.isBefore(endTime)) {
            // 获取时间内的数据
            List<SanitationTruckLocation> data = sanitationTruckLocationService
                    .getStream(smsCode, nextTime.format(df), nextTime.plusSeconds(intervalTimes).format(df))
                    .collect(Collectors.toList());
            // 保存记录
            TruckDistanceRecord record = new TruckDistanceRecord();
            record.setDistance(genDistance(data).intValue());
            record.setOnlineTimes(genOnlineTimes(data).intValue());
            record.setSmsCode(smsCode);
            record.setStartTime(nextTime);
            record.setEndTime(nextTime.plusSeconds(intervalTimes));
            record.setUpdateTime(LocalDateTime.now());
            truckDistanceRecordDao.save(record);

            // 更新下次时间
            nextTime = nextTime.plusSeconds(intervalTimes);
        }
        log.info("计算[{}]-[{}]的行驶记录结束", smsCode, date);
    }

    /**
     * 计算里程
     */
    private Long genDistance(List<SanitationTruckLocation> data) {
        int index = 0;
        long distance = 0L;
        for (SanitationTruckLocation item : data) {
            if (index > 0) {
                SanitationTruckLocation prev = data.get(index - 1);
                distance = distance + getDistance(prev.getLat(), prev.getLng(), item.getLat(), item.getLng());
            }
            index = index + 1;
        }
        return distance;
    }

    /**
     * 计算两个坐标点之间的距离，单位：厘米
     */
    private Long getDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        BigDecimal divisor = BigDecimal.valueOf(180.0);

        BigDecimal radLat1 = lat1.multiply(BigDecimal.valueOf(Math.PI)).divide(divisor, 10, RoundingMode.HALF_UP);
        BigDecimal radLat2 = lat2.multiply(BigDecimal.valueOf(Math.PI)).divide(divisor, 10, RoundingMode.HALF_UP);
        BigDecimal lats = radLat1.subtract(radLat2);

        BigDecimal radLng1 = lng1.multiply(BigDecimal.valueOf(Math.PI)).divide(divisor, 10, RoundingMode.HALF_UP);
        BigDecimal radLng2 = lng2.multiply(BigDecimal.valueOf(Math.PI)).divide(divisor, 10, RoundingMode.HALF_UP);
        BigDecimal lngs = radLng1.subtract(radLng2);

        double s = (2 * Math.asin(Math.sqrt(Math.pow(Math.sin(lats.doubleValue() / 2), 2) +
                Math.cos(radLat1.doubleValue()) * Math.cos(radLat2.doubleValue()) * Math.pow(Math.sin(lngs.doubleValue() / 2), 2)))
        ) * 6378.137;
        return Math.round(s * 1000 * 100);
    }

    /**
     * 计算acc在线时间，单位：秒
     */
    private Long genOnlineTimes(List<SanitationTruckLocation> data) {
        List<List<SanitationTruckLocation>> temp = new ArrayList<>();
        temp.add(new ArrayList<>());
        for (SanitationTruckLocation item : data) {
            temp.get(temp.size() - 1).add(item);
            if (item.getAcc() == 0) {
                temp.add(new ArrayList<>());
            }
        }
        return temp.stream()
                .filter(item -> item.size() > 2)
                .map(item -> {
                    SanitationTruckLocation first = item.get(0);
                    SanitationTruckLocation last = item.get(item.size() - 1);
                    LocalDateTime startTime = LocalDateTime.parse(first.getAddTime(), df);
                    LocalDateTime endTime = LocalDateTime.parse(last.getAddTime(), df);
                    return Duration.between(startTime, endTime).getSeconds();
                })
                .reduce(0L, Long::sum);
    }
}
