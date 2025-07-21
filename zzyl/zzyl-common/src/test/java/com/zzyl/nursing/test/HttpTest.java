package com.zzyl.nursing.test;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
public class HttpTest {

    @Test
    public void testGet(){
        String result = HttpUtil.get("https://www.badiu.com");
        System.out.println(result);
    }

    @Test
    public void testGetByParam(){
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",1);
        map.put("pageSize",10);
        String result = HttpUtil.get("https://www.badiu.com",map);
    }

    //手动构建查询参数
    @Test
    public void testCreateRequest(){
        Map<String,Object> map = new HashMap<>();
        map.put("pageNum",1);
        map.put("pageSize",10);
        //分页查询护理项目
        HttpResponse response = HttpUtil.createRequest(Method.GET, "http://localhost:8080/serve/project/list")
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImUyNTgyNzdhLTMxNzAtNGFhNS05MmNhLWRiNjFiNDYyOGYxNCJ9.p8ieeQe4A6lozsxpzTL4Z3JNAS3UgBOVRZckDFnNvJQs8C2I5X5nZVxAQCaUv_0u9yUN-d0FKDCHwtCkMeIdBA")
                .form(map)
                .execute();
        if(response.isOk()){
            System.out.println(response.body());
        }
    }
}
