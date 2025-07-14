package com.zzyl.nursing.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 合同对象 contract
 * 
 * @author ruoyi
 * @date 2025-07-14
 */
@Data 
public class Contract extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 合同名称 */
    @Excel(name = "合同名称")
    private String contractName;

    /** 合同编号 */
    @Excel(name = "合同编号")
    private String contractNumber;

    /** 协议地址（文件路径或URL） */
    @Excel(name = "协议地址", readConverterExp = "文=件路径或URL")
    private String agreementPath;

    /** 丙方手机号 */
    @Excel(name = "丙方手机号")
    private String thirdPartyPhone;

    /** 丙方姓名 */
    @Excel(name = "丙方姓名")
    private String thirdPartyName;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime startDate;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime endDate;

    /** 状态 (0: 未生效, 1: 已生效, 2: 已过期, 3: 已失效) */
    @Excel(name = "状态 (0: 未生效, 1: 已生效, 2: 已过期, 3: 已失效)")
    private Integer status;

    /** 签约日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "签约日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime signDate;

    /** 解除提交人 */
    @Excel(name = "解除提交人")
    private String terminationSubmitter;

    /** 解除日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "解除日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime terminationDate;

    /** 解除协议地址（文件路径或URL） */
    @Excel(name = "解除协议地址", readConverterExp = "文=件路径或URL")
    private String terminationAgreementPath;

    /** 排序编号 */
    @Excel(name = "排序编号")
    private Integer sortOrder;

}
