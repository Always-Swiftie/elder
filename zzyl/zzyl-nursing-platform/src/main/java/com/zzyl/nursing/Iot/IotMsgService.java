package com.zzyl.nursing.Iot;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@Data
public class IotMsgService {

    @JsonProperty("service_id")
    private String serviceId;

    private Map<String, Object> properties;

    @JsonProperty("event_time")
    private String eventTime;
}

