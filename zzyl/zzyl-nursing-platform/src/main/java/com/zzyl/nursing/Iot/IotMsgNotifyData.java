package com.zzyl.nursing.Iot;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class IotMsgNotifyData {

    private IotMsgHeader header;

    private IotMsgBody body;
}

