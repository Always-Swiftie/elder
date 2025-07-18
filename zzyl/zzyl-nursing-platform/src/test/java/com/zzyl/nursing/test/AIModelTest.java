package com.zzyl.nursing.test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
public class AIModelTest {

    private static final String prompt = "你能帮我分析一份完整的老人体检报告吗?告知我格式应该怎么写";

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String appkey = "lzinmbnkvu7bvwmsxluhflszlq5ib6eg7gyjseyo";
        String udid = UUID.randomUUID().toString();
        String secret = "bc204afa87e61a93939f5f2ac2d98770";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = getSign(appkey, udid, timestamp, secret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("appkey", appkey);
        headers.set("requestId", UUID.randomUUID().toString());
        headers.set("udid", udid);
        headers.set("timestamp", timestamp);
        headers.set("sign", sign);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "unigpt-3.5");
        requestBody.put("stream", false);
        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", prompt)  // 你传的具体内容
        );
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://unigpt-api.hivoice.cn/rest/v1.1/chat/completions", entity, String.class
        );
        System.out.println(response.getBody());

    }

    public static String getSign(String appkey, String udid, String timestamp, String secret) {
        String originalStr = appkey + udid + timestamp + secret;
        StringBuilder sign = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(originalStr.getBytes(StandardCharsets.UTF_8));
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) sign.append("0");
                sign.append(hex.toUpperCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign.toString();
    }


}
