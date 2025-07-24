package com.zzyl.nursing.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 20784
 */
@Data
public class ProductPageVo {

    /**
     * 产品的ProductKey,物联网平台产品唯一标识
     */

    @ApiModelProperty("产品的ProductKey,物联网平台产品唯一标识")
    private String productKey;

    /**
     * 产品名称
     */
    @ApiModelProperty("产品名称")
    private String productName;
}
