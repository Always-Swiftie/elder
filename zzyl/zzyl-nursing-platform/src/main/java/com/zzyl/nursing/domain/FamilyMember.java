package com.zzyl.nursing.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 老人家属对象 family_member
 * 
 * @author ruoyi
 * @date 2025-07-21
 */
@Data
@Builder
public class FamilyMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 头像 */
    @Excel(name = "头像")
    private String avatar;

    /** OpenID */
    @Excel(name = "OpenID")
    private String openId;

    /** 性别(0:男，1:女) */
    @Excel(name = "性别(0:男，1:女)")
    private Integer gender;

}
