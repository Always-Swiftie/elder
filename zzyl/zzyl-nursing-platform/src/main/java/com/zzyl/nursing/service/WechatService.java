package com.zzyl.nursing.service;

import org.springframework.stereotype.Service;

/**
 * @author 20784
 */
@Service
public interface WechatService {

    /**
     * 获取openid
     * @param code
     * @return
     */
    public String getOpenId(String code);

    /**
     * 获取手机号
     * @param detailCode
     * @return
     */
    public String getPhone(String detailCode);

}
