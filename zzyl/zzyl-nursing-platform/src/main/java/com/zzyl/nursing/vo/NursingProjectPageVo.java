package com.zzyl.nursing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 20784
 */
@Data
public class NursingProjectPageVo {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 护理项目名称
     */
    private String name;

    /**
     * 排序编号
     */
    private Integer orderNo;

    /**
     * 单位
     */
    private String unit;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 图片
     */
    private String image;

    /**
     * 护理要求
     */
    private String nursingRequirement;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建者id
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新者id
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 备注
     */
    private String remark;
}
