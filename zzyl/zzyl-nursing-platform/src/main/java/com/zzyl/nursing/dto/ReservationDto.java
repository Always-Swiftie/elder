package com.zzyl.nursing.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 20784
 */
@Data
public class ReservationDto {

    /**
     * 手机号
     */
    String mobile;

    /**
     * 预约人姓名
     */
    String name;

    /**
     * 预约时间
     */
    String time;

    /**
     * 预约类型
     */
    Integer type;

    /**
     * 家人姓名
     */
    String visitor;

}
