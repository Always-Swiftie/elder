package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 20784
 */
@Data
@ApiModel("查询设备详情请求参数")
public class DeviceInfoQueryDto {

    @ApiModelProperty("设备的 iotId")
    private String iotId;

    @ApiModelProperty("产品的 productKey")
    private String productKey;
}
