package com.daidai.equipmentplatform.distancestatistic.web;

import com.daidai.equipmentplatform.distancestatistic.dao.SanitationTruckLocationDao;
import com.daidai.equipmentplatform.distancestatistic.model.SanitationTruckLocation;
import com.daidai.equipmentplatform.distancestatistic.service.ISanitationTruckLocationService;
import com.daidai.equipmentplatform.distancestatistic.service.ITruckDistanceRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther lirenhao
 * @Date 2022/10/11 下午6:14
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private ISanitationTruckLocationService sanitationTruckLocationService;
    @Resource
    private SanitationTruckLocationDao sanitationTruckLocationDao;
    @Resource
    private ITruckDistanceRecordService truckDistanceRecordService;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/list")
    public List<String> list() {
        return sanitationTruckLocationService.getSmsCodes("2022-10-11 00:00:00", "2022-10-12 00:00:00");
    }

    @GetMapping("/temp")
    public List<SanitationTruckLocation> temp(@RequestParam String smsCode) {
        return sanitationTruckLocationService.getList(
                smsCode, "2022-10-11 00:00:00", "2022-10-12 00:00:00");
    }

    @GetMapping("/dao")
    public List<SanitationTruckLocation> dao(@RequestParam String smsCode) {
        Stream<SanitationTruckLocation> stream = sanitationTruckLocationService.getStream(
                smsCode, "2022-10-11 00:00:00", "2022-10-12 00:00:00");
        return stream.collect(Collectors.toList());
    }

    @GetMapping("/add")
    public void add() {
        int total = 86400 / 5;
        for(int i = 0; i < total ; i = i +1){
            SanitationTruckLocation record = new SanitationTruckLocation();
            record.setSmsCode("1234567890");
            record.setLat(BigDecimal.valueOf(34.796455217839124));
            record.setLng(BigDecimal.valueOf(113.68198344519548));
            record.setSpeed(0);
            record.setHeight(0);
            record.setDirection(0);
            record.setAddTime(LocalDateTime.parse("2022-10-11 00:00:00", df).plusSeconds(i * 5).format(df));
            record.setAcc(0);
            sanitationTruckLocationDao.save(record);
        }
    }

    @GetMapping("/cmd")
    public void cmd(@RequestParam String date) {
        truckDistanceRecordService.handleAllData(date);
    }
}
