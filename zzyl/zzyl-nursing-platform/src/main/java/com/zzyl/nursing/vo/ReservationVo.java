package com.zzyl.nursing.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 20784
 */
@Data
public class ReservationVo {

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
    String mobile;

    /**
     * 时间
     */
    LocalDateTime time;

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
     * 创建人id
     */
    Long creatBy;

    /**
     * 修改人id
     */
    Long updateBy;

    /**
     * 修改时间
     */
    LocalDateTime updateTime;

    /**
     * 备注
     */
    String remark;
}
