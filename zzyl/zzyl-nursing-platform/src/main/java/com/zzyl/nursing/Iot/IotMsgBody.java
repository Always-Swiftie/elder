package com.zzyl.nursing.Iot;

import lombok.Data;
import java.util.List;

@Data
public class IotMsgBody {
    private List<IotMsgService> services;
}

