package com.daidai.equipmentplatform.distancestatistic.model;

import com.daidai.equipmentplatform.distancestatistic.constant.CommonConstants;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Document(indexName = CommonConstants.SANITATION_TRUCK_LOCATION_INDEX)
public class SanitationTruckLocation {

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * SMS编号
     */
    @Field(type = FieldType.Keyword)
    private String smsCode;

    /**
     * 纬度
     */
    @Field(type = FieldType.Keyword)
    private BigDecimal lat;

    /**
     * 经度
     */
    @Field(type = FieldType.Keyword)
    private BigDecimal lng;

    /**
     * 高度
     */
    @Field(type = FieldType.Integer)
    private Integer height;

    /**
     * 速度
     */
    @Field(type = FieldType.Integer)
    private Integer speed;

    /**
     * 方向
     */
    @Field(type = FieldType.Integer)
    private Integer direction;

    /**
     * 添加时间
     */
    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private String addTime;

    /**
     * acc状态为 0-关 1-开
     */
    @Field(type = FieldType.Integer)
    private Integer acc;
}
