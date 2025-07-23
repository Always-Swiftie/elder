package com.zzyl.nursing.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 20784
 */
@Data
public class Reservation {

    /**
     * 主键id
     */
    Long id;

    /**
     * 名称
     */
    String name;

    /**
     * 联系人电话
     */
    String phone;

    /**
     * 时间
     */
    String time;

    /**
     * 访客
     */
    String visitor;

    /**
     * 类型
     */
    Integer type;

    /**
     * 状态
     */
    Integer status;

    /**
     * 预约创建时间
     */
    LocalDateTime createTime;

    /**
     * 预约修改时间
     */
    LocalDateTime updateTime;

    /**
     * 创建人id
     */
    Long creatBy;

    /**
     * 修改人id
     */
    Long updateBy;

    /**
     * 备注
     */
    String remark;
}
