package com.zzyl.nursing.Iot;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class IotMsgHeader {

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("gateway_id")
    private String gatewayId;
}

