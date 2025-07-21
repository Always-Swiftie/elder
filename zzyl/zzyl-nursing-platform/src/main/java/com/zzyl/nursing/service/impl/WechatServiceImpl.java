package com.zzyl.nursing.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.nursing.service.WechatService;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 20784
 */
@Service
public class WechatServiceImpl implements WechatService {

    //login
    private static final String REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";
    //get token
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
    //get phone number
    private static final String PHONE_REQUEST_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String secret;

    /**
     * 获取openId(登录用户的唯一标识)
     * @param code
     * @return
     */
    @Override
    public String getOpenId(String code) {
        Map<String, Object> paramMap = getAppConfig();
        paramMap.put("js_code", code);

        String result = HttpUtil.get(REQUEST_URL, paramMap);
        //响应结果集
        JSONObject jsonObject = JSONUtil.parseObj(result);
        //判错
        if (ObjectUtils.isNotEmpty(jsonObject.get("errcode"))) {
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //获取openId
        String openid = jsonObject.getStr("openid");
        return openid;
    }

    /**
     * 封装公共参数
     * @param
     * @return
     */
    private Map<String,Object> getAppConfig(){
        Map<String,Object> parammap = new HashMap<>();
        parammap.put("appid",appId);
        parammap.put("secret",secret);
        return parammap;
    }

    /**
     * 获取登录用户的手机号
     * @param detailCode
     * @return
     */
    @Override
    public String getPhone(String detailCode) {
        String token = getToken();
        String url = PHONE_REQUEST_URL + token;
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("code",detailCode);
        //发起请求
        String result = HttpUtil.post(url,JSONUtil.toJsonStr(paramMap));
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(jsonObject.getInt("errcode")!=0){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //从响应的json数据phone_info 中拿到phoneNumber这个字段
        return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
    }

    /**
     * 获取token
     * @return
     */
    private String getToken(){
        Map<String,Object> paramMap = getAppConfig();
        //发起请求
        String result = HttpUtil.get(TOKEN_URL,paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(ObjectUtils.isNotEmpty(jsonObject.get("errcode"))) {
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        String token = jsonObject.getStr("access_token");
        return token;
    }

}
