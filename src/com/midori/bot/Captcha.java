package com.midori.bot;

import com.midori.database.DBSetTools;
import com.midori.ui.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Captcha {


    static class Response {
        public List<NameValuePair> params;
        public int taskId;
    }

    final private static int INITIAL_WAIT_TIME = 6400;
    final private static int CHECK_LOOP_TIME = 2612;
    final private static int MAX_CHECK_COUNT = 32;

    final private static String urlCreateTask = "http://api.anti-captcha.com/createTask";
    final private static String urlGetTaskResult = "http://api.anti-captcha.com/getTaskResult";
    final private static String urlReportIncorrectImageCaptcha = "http://api.anti-captcha.com/reportIncorrectImageCaptcha";

    private static CloseableHttpClient client = HttpClients.custom()
            .disableCookieManagement()
            .disableContentCompression()
            .addInterceptorLast((HttpRequestInterceptor) (request, context) -> {
                request.removeHeaders("Origin");
                request.removeHeaders("User-Agent");
            })
            .disableAutomaticRetries()
            .build();

    public static int sendImageCaptcha(String image) throws IOException {
        Log.Print(Log.t.DBG, "Creating ImageToTextTask Captcha task...");
        HttpPost posst = new HttpPost(urlCreateTask);
        HttpEntity body = new StringEntity(new JSONObject()
                .put("clientKey", DBSetTools.SET_ANTICAPTCHA_KEY)
                .put("task", new JSONObject()
                        .put("type", "ImageToTextTask")
                        .put("body", image)
                        .put("phase", false)
                        .put("case", false)
                        .put("numeric", 0)
                        .put("math", false)
                        .put("minLength", 6)
                        .put("maxLength", 6)).toString(), ContentType.APPLICATION_JSON);
        posst.setEntity(body);
        try (CloseableHttpResponse response = client.execute(posst)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JSONObject result = new JSONObject(EntityUtils.toString(entity));
                if (result.getInt("errorId") == 0) {
                    int taskId = result.getInt("taskId");
                    Log.Print(Log.t.DBG, "(" + taskId + "): Captcha task created");
                    return taskId;
                } else {
                    String error = result.getString("errorCode") + ": " + result.getString("errorDescription");
                    Log.Print(Log.t.ERR, "Captcha task can not created: " + error);
                    throw new RuntimeException(error);
                }
            }
        }
        return -1;
    }

    public static String checkCaptcha(int taskId) throws IOException, InterruptedException {
        String error;
        HttpPost post = new HttpPost(urlGetTaskResult);
        HttpEntity body = new StringEntity(new JSONObject()
                .put("clientKey", DBSetTools.SET_ANTICAPTCHA_KEY)
                .put("taskId", taskId).toString(), ContentType.APPLICATION_JSON);
        post.setEntity(body);
        Thread.sleep(INITIAL_WAIT_TIME);
        Log.Print(Log.t.DBG, "(" + taskId + "): Entering checking captcha task loop...");
        for (int i = 0; i < MAX_CHECK_COUNT; i++) {
            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    JSONObject result = new JSONObject(EntityUtils.toString(entity));
                    String status = result.getString("status");
                    if (result.getInt("errorId") == 0) {
                        if (status.equals("ready")) {
                            Log.Print(Log.t.SCS, "(" + taskId + "): Captcha solved in " +
                                    (result.getLong("endTime") - result.getLong("createTime")) + " seconds. " +
                                    "Cost: $" + String.format("%.6f", result.getDouble("cost")));
                            return result.getJSONObject("solution").getString("text");
                        } else if (status.equals("processing")) {
                            Thread.sleep(CHECK_LOOP_TIME);
                        } else {
                            error = "Unknown status: " + status;
                            Log.Print(Log.t.ERR, error);
                            throw new RuntimeException(error);
                        }
                    } else {
                        error = result.getString("errorCode") + ": " + result.getString("errorDescription");
                        Log.Print(Log.t.ERR, "Captcha task can not checked: " + error);
                        throw new RuntimeException(error);
                    }
                }
            }
            if (i == (MAX_CHECK_COUNT - 1)) {
                error = "Timeout: Reached MAX_CHECK_COUNT";
                Log.Print(Log.t.ERR, error);
                throw new RuntimeException(error);
            }
        }
        return null;
    }

    public static void reportIncorrectImageCaptcha(int taskId) throws IOException {
        Log.Print(Log.t.DBG, "(" + taskId + "): Captcha reporting...");
        HttpPost post = new HttpPost(urlReportIncorrectImageCaptcha);
        HttpEntity body = new StringEntity(new JSONObject()
                .put("clientKey", DBSetTools.SET_ANTICAPTCHA_KEY)
                .put("taskId", taskId).toString(), ContentType.APPLICATION_JSON);
        post.setEntity(body);
        try (CloseableHttpResponse response = client.execute(post)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                JSONObject result = new JSONObject(EntityUtils.toString(entity));
                if (result.getInt("errorId") == 0) {
                    Log.Print(Log.t.SCS, "(" + taskId + "): Captcha reported");
                } else {
                    Log.Print(Log.t.ERR, "Captcha can not be reported: " + result.getInt("errorId"));
                    throw new RuntimeException(String.valueOf(result.getInt("errorId")));
                }
            }
        }
    }
}


