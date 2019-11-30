package com.midori.bot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class Captcha {

    final private static String urlCreateTask = "http://api.anti-captcha.com/createTask";
    private static CloseableHttpClient client = HttpClients.custom()
            .disableCookieManagement()
            .disableContentCompression()
            .addInterceptorLast((HttpRequestInterceptor) (request, context) -> {
                request.removeHeaders("Origin");
                request.removeHeaders("User-Agent");
            })
            .build();

    public static void main(String[] args) throws IOException {
        solveImageCaptcha();
    }

    public static void solveImageCaptcha() throws IOException {
        HttpPost request = new HttpPost(urlCreateTask);
        HttpEntity stringEntity = new StringEntity(new JSONObject()
                .put("clientKey", "5efd3fe7179bdf7d576bf709d4b698c6") //DBSetTools.SET_ANTICAPTCHA_KEY
                .put("task", new JSONObject()
                        .put("type", "ImageToTextTask")
                        .put("body", "dsasf")
                        .put("phase", false)
                        .put("case", false)
                        .put("numeric", 0)
                        .put("math", false)
                        .put("minLength", 6)
                        .put("maxLength", 6)).toString(), ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        try (CloseableHttpResponse response = client.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JSONObject result = new JSONObject(EntityUtils.toString(entity));
                if (result.getInt("errorId") == 0) {
                    int taskID = result.getInt("taskId");


                    // here countine
                    System.out.println(result.toString());
                    System.out.println(taskID);
                } else {
                    throw new RuntimeException(result.getString("errorCode") + ": " + result.getString("errorDescription"));
                }
            }
        }
    }

}


