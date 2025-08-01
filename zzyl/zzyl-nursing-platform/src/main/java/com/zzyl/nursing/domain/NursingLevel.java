package com.zzyl.nursing.domain;

import java.math.BigDecimal;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 护理等级对象 nursing_level
 *
 * @author ruoyi
 * @date 2025-07-13
 */
@Data
public class NursingLevel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 等级名称 */
    @Excel(name = "等级名称")
    private String name;

    /** 护理计划ID */
    @Excel(name = "护理计划ID")
    private Integer lplanId;

    /** 护理费用 */
    @Excel(name = "护理费用")
    private BigDecimal fee;

    /** 状态（0：禁用，1：启用） */
    @Excel(name = "状态", readConverterExp = "0=：禁用，1：启用")
    private Integer status;

    /** 等级说明 */
    @Excel(name = "等级说明")
    private String description;

}