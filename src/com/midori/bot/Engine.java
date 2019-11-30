package com.midori.bot;

import com.anti_captcha.Api.ImageToText;
import com.anti_captcha.Helper.DebugHelper;
import com.midori.ui.main.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Engine {

    private static class URL {
        final private static String home = "https://freebitco.in/?op=home";
        final private static String root = "https://freebitco.in/";
        final private static String signUp = "https://freebitco.in/?op=signup_page";
        final private static String statsNewPrivate = "https://freebitco.in/stats_new_private/";
        final private static String api = "https://freebitco.in/cgi-bin/api.pl";
        final private static String fpCheck = "https://freebitco.in/cgi-bin/fp_check.pl";
    }

    private static class Header {
        final private static BasicHeader acceptDefault = new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        final private static BasicHeader acceptAll = new BasicHeader(HttpHeaders.ACCEPT, "*/*");
        final private static BasicHeader acceptImage = new BasicHeader(HttpHeaders.ACCEPT, "image/webp,*/*");
        final private static BasicHeader basicOrigin = new BasicHeader("Origin", "https://freebitco.in");
        final private static BasicHeader xreqXML = new BasicHeader("X-Requested-With", "XMLHttpRequest");
        final private static String xCsrfToken = "X-Csrf-Token";
    }


    public static void Login(Account account) throws IOException, LoginError {
        Log.Print(Log.t.DBG, account.logDomain() + "Loading initial page for login...");
        HttpGet get = new HttpGet(URL.signUp);
        get.addHeader(Header.acceptDefault);
        account.httpClient.execute(get, account.httpClientContext).close();
        Log.Print(Log.t.DBG, account.logDomain() + "Logging in...");
        HttpPost post = new HttpPost(URL.root);
        post.addHeader(Header.acceptAll);
        post.addHeader(Header.basicOrigin);
        post.addHeader(HttpHeaders.REFERER, URL.signUp);
        post.addHeader(Header.xCsrfToken, account.getCSRFToken());
        post.addHeader(Header.xreqXML);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("csrf_token", account.getCSRFToken()));
        params.add(new BasicNameValuePair("op", "login_new"));
        params.add(new BasicNameValuePair("btc_address", account.emailProperty().get()));
        params.add(new BasicNameValuePair("password", account.password));
        params.add(new BasicNameValuePair("tfa_code", "")); //todo: support tfa code
        post.setEntity(new UrlEncodedFormEntity(params));
        try (CloseableHttpResponse response = account.httpClient.execute(post, account.httpClientContext)) {
            if (response.getStatusLine().getStatusCode() != 200) throw new LoginError("Unexcepted status code");
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity);
                if (body.charAt(0) == 's' && StringUtils.countMatches(body, ':') == 3) {
                    String[] vals = StringUtils.split(body, ':');
                    account.setID(Integer.parseInt(vals[3]));
                    account.btcAddress = vals[1];
                    account.setloginCredentials(vals[2]);
                } else if (body.charAt(0) == 'e') throw new LoginError(body.substring(body.lastIndexOf(':') + 1));
                else throw new LoginError("Unexcepted response");
            } else {
                throw new LoginError("Empty body");
            }
        }
    }

    public static void Home(Account account) throws IOException, URISyntaxException {
        Log.Print(Log.t.DBG, account.logDomain() + "Loading home...");
        HttpGet get1 = new HttpGet(URL.root);
        get1.addHeader(Header.acceptDefault);
        get1.addHeader(HttpHeaders.REFERER, URL.root);
        try (CloseableHttpResponse response = account.httpClient.execute(get1, account.httpClientContext)) {
            if (response.getStatusLine().getStatusCode() != 200)
                throw new RuntimeException("Unexcepted status code"); //todo ban check
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity);
                Document doc = Jsoup.parse(body);

                //@parseller:1
                account.setBalance(Double.parseDouble(doc.selectFirst("#balance").text()));

                //@parseller:2
                account.setRewardPoints(Integer.parseInt(doc.selectFirst(".user_reward_points").text().replaceAll(",", "")));

                //@parseller:3
                account.setFpBonusReqCompleted(Double.parseDouble(doc.selectFirst("#fp_bonus_req_completed").text()));

                //@parseller:4
                account.disableLottery = doc.selectFirst("#disable_lottery_checkbox").attr("checked").equals("checked");

                //@parseller:5
                account.socketPassword = StringUtils.substringBetween(body, "socket_password = '", "'");

                //@parseller:6
                account.tokenName = StringUtils.substringBetween(body, "token_name = '", "'");

                //@parseller:7
                account.secretTokenName = StringUtils.substringBetween(body, "tcGiQefA = '", "'");

                //@parseller:8
                account.secretToken = StringUtils.substringBetween(body, "um2VHVjSZ = \"", "\"");

                //@parseller:9
                account.token = StringUtils.substringBetween(body, "token1 = '", "'");

                //@parseller:10
                account.fpAvailable = Integer.parseInt(StringUtils.substringBetween(body, "free_play = ", ";"));
                account.captchaType = Integer.parseInt(StringUtils.substringBetween(body, "captcha_type = ", ";"));
                account.multiAcctSameIP = Integer.parseInt(StringUtils.substringBetween(body, "multi_acct_same_ip = ", ";"));
                account.show2FAMsg = Integer.parseInt(StringUtils.substringBetween(body, "show_2fa_msg = ", ";"));

                //@parseller:11
                if (account.fpAvailable == 0) {
                    int countdown = Integer.parseInt(StringUtils.substringBetween(body, ".free_play_time_remaining').countdown({until: ", ","));
                    account.lastFPDate = new java.sql.Date(DateUtils.addSeconds(new Date(), -(3600 - countdown)).getTime());
                } else {
                    account.lastFPDate = new java.sql.Date(DateUtils.addHours(new Date(), -3).getTime());
                }
            }
        }

        Log.Print(Log.t.DBG, account.logDomain() + "Loading initial stats...");
        JSONObject stats = new JSONObject();
        HttpGet get2 = new HttpGet(URL.statsNewPrivate);
        get2.setURI(new URIBuilder(get2.getURI())
                .addParameter("u", account.idProperty().asString().get())
                .addParameter("p", account.socketPassword)
                .addParameter("f", "user_stats_initial")
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get2.addHeader(Header.acceptAll);
        get2.addHeader(HttpHeaders.REFERER, URL.root);
        get2.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get2.addHeader(Header.xreqXML);
        try (CloseableHttpResponse response = account.httpClient.execute(get2, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //@parseller:j1
                stats.put("user_stats_initial", new JSONObject(EntityUtils.toString(entity)));
                account.setFpPlayed(stats.getJSONObject("user_stats_initial")
                        .getJSONObject("user").getInt("free_spins_played"));
            }
        }

        Log.Print(Log.t.DBG, account.logDomain() + "Recording fingerprint...");
        HttpGet get3 = new HttpGet(URL.api);
        get3.setURI(new URIBuilder(get3.getURI())
                .addParameter("op", "record_fingerprint")
                .addParameter("fingerprint", account.fingerprint)
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get3.addHeader(Header.acceptAll);
        get3.addHeader(HttpHeaders.REFERER, URL.root);
        get3.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get3.addHeader(Header.xreqXML);
        account.httpClient.execute(get3, account.httpClientContext).close();

        Log.Print(Log.t.DBG, account.logDomain() + "Recording time offset...");
        HttpGet get4 = new HttpGet(URL.api);
        get4.setURI(new URIBuilder(get4.getURI())
                .addParameter("op", "record_user_data")
                .addParameter("type", "time_offset")
                .addParameter("value", String.valueOf(account.timeOffset))
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get4.addHeader(Header.acceptAll);
        get4.addHeader(HttpHeaders.REFERER, URL.root);
        get4.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get4.addHeader(Header.xreqXML);
        account.httpClient.execute(get4, account.httpClientContext).close();

        Log.Print(Log.t.DBG, account.logDomain() + "Getting FP Token...");
        HttpGet get5 = new HttpGet(URL.fpCheck);
        get5.setURI(new URIBuilder(get5.getURI())
                .addParameter("s", account.secretTokenName)
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get5.addHeader(Header.acceptAll);
        get5.addHeader(HttpHeaders.REFERER, URL.root);
        get5.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get5.addHeader(Header.xreqXML);
        try (CloseableHttpResponse response = account.httpClient.execute(get5, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                account.fpToken = EntityUtils.toString(entity);
            }
        }

        Log.Print(Log.t.DBG, account.logDomain() + "Loading stats...");
        HttpGet get6 = new HttpGet(URL.statsNewPrivate);
        get6.setURI(new URIBuilder(get6.getURI())
                .addParameter("u", account.idProperty().asString().get())
                .addParameter("p", account.socketPassword)
                .addParameter("f", "user_stats")
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get6.addHeader(Header.acceptAll);
        get5.addHeader(HttpHeaders.REFERER, URL.root);
        get6.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get6.addHeader(Header.xreqXML);
        try (CloseableHttpResponse response = account.httpClient.execute(get6, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //@parseller:j2
                stats.put("user_stats", new JSONObject(EntityUtils.toString(entity)));
            }
        }

        account.stats = stats.toString();
    }

    public static void Roll(Account account) throws IOException, URISyntaxException, InterruptedException, RollError {
        List<NameValuePair> params = new ArrayList<>();
        if (account.captchaType == 0) {
            Log.Print(Log.t.DBG, account.logDomain() + "Rolling without captcha...");
            params.add(new BasicNameValuePair("pwc", "1"));
        } else if (account.captchaType == 11) {
            if (account.useRPForFP && account.fpRpCost < 8  //todo: 8 will be global
                    && account.rewardPointsProperty().get() >= account.fpRpCost) {
                Log.Print(Log.t.DBG, account.logDomain() + "Rolling with RP...");
                //todo?
            } else {
                Log.Print(Log.t.DBG, account.logDomain() + "Rolling with captcha...");
                params.add(new BasicNameValuePair("pwc", "0"));
                String[] captcha1 = getAndSolveBotdetectCaptcha(account);
                String[] captcha2 = getAndSolveBotdetectCaptcha(account);
                params.add(new BasicNameValuePair("botdetect_random", captcha1[0]));
                params.add(new BasicNameValuePair("botdetect_response", captcha1[1]));
                params.add(new BasicNameValuePair("botdetect_random2", captcha2[0]));
                params.add(new BasicNameValuePair("botdetect_response2", captcha2[1]));
            }
        }

        HttpPost post = new HttpPost(URL.root);
        post.addHeader(Header.acceptAll);
        post.addHeader(HttpHeaders.REFERER, URL.root);
        post.addHeader(Header.xCsrfToken, account.getCSRFToken());
        post.addHeader(Header.xreqXML);
        params.add(new BasicNameValuePair("csrf_token", account.getCSRFToken()));
        params.add(new BasicNameValuePair("op", "free_play"));
        params.add(new BasicNameValuePair("fingerprint", account.fingerprint));
        params.add(new BasicNameValuePair("client_seed", RandomStringUtils.random(16, Rune.seedBytes)));
        params.add(new BasicNameValuePair("fingerprint2", String.valueOf(account.fingerprint2)));
        params.add(new BasicNameValuePair(account.tokenName, account.token));
        params.add(new BasicNameValuePair(account.secretTokenName, DigestUtils.sha256Hex(account.fpToken)));

        post.setEntity(new UrlEncodedFormEntity(params));
        try (CloseableHttpResponse response = account.httpClient.execute(post, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity);
                if (body.charAt(0) == 's') {
                    String[] vals = StringUtils.split(body, ':');
                    //s:4575:0.00000137:0.00000027:1574947894:0:215dc0aa218e2e24a34aac8aec34f19ae64b1aa7afe0d76fad34bca1178c9483:IUuSHJ01guTg7tm9:9337:756bb738d5a82ead4536403b239977c232297a121a886d701f759aa9adc0dcdc:09c39e9a3227b6bb52697f74d8aaa0f975f17deae7d6a1480ac32b2b1ea3ad2b:IUuSHJ01guTg7tm9:9336:50:19841:2:2:0.00000000:62.8423

                    //todo: add half of vals[3] to master account
                    Log.Print(Log.t.SCS, account.logDomain() + "Rolled: " + vals[1] + ", earned " + vals[3] + ", " +
                            vals[16] + " RP, " + vals[15] + " lottery tickets");
                    account.setBalance(Double.parseDouble(vals[2]));
                    account.addCookie("last_play", vals[4]);
                    account.lastFPDate = new java.sql.Date(System.currentTimeMillis());
                    account.setRewardPoints(Integer.parseInt(vals[14]));
                    account.setFpBonusReqCompleted(Double.parseDouble(vals[18]));
                    //todo: need append to log account

                    //todo: check captcha error and try again
                    //todo: check same ip error and calculate wait time
                } else if (body.charAt(0) == 'e') throw new RollError(body.substring(body.lastIndexOf(':') + 1));
                else throw new RollError("Unexcepted response");
            }
        }
    }


    public static String[] getAndSolveBotdetectCaptcha(Account account) throws URISyntaxException, IOException, InterruptedException {
        String random = "";
        String result = "";
        String base64 = "";

        HttpGet get1 = new HttpGet(URL.api);
        get1.setURI(new URIBuilder(get1.getURI())
                .addParameter("op", "generate_captchasnet")
                .addParameter("f", account.fingerprint)
                .addParameter("csrf_token", account.getCSRFToken()).build());
        get1.addHeader(Header.acceptAll);
        get1.addHeader(HttpHeaders.REFERER, URL.root);
        get1.addHeader(Header.xCsrfToken, account.getCSRFToken());
        get1.addHeader(Header.xreqXML);
        try (CloseableHttpResponse response = account.httpClient.execute(get1, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                random = EntityUtils.toString(entity);
            }
        }

        HttpGet get2 = new HttpGet("https://captchas.freebitco.in/botdetect/e/live/index.php");
        get2.setURI(new URIBuilder(get2.getURI())
                .addParameter("random", random).build());
        get2.addHeader(Header.acceptImage);
        get2.addHeader(HttpHeaders.REFERER, URL.root);
        try (CloseableHttpResponse response = account.httpClient.execute(get2, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                base64 = new String(Base64.getEncoder().encode(EntityUtils.toByteArray(entity)));
                ImageToText api = new ImageToText();
                api.setClientKey("5efd3fe7179bdf7d576bf709d4b698c6");
                api.setBodyBase64(base64);
                if (!api.createTask()) {
                    System.out.println(api.getErrorMessage());
                } else if (!api.waitForResult()) {
                    DebugHelper.out("Could not solve the captcha.", DebugHelper.Type.ERROR);
                } else {
                    result = api.getTaskSolution().getText();
                }
            }
        }
        return new String[]{random, result, base64};
    }

    public static String Open(Account account, String url) throws IOException {
        Log.Print(Log.t.DBG, account.logDomain() + "Openning link with account's engine...");
        String body = "";
        HttpGet get = new HttpGet(url);
        get.addHeader(Header.acceptDefault);
        try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity);
            }
        }
        return body;
    }

    static void PurchaseLottTickets(Account account, int amount) throws IOException, URISyntaxException {
        //todo: uri & error handling
        try {
            HttpGet get = new HttpGet("https://freebitco.in/");
            get.setURI(new URIBuilder(get.getURI())
                    .addParameter("op", "purchase_lott_tickets")
                    .addParameter("num", String.valueOf(amount))
                    .addParameter("csrf_token", account.getCSRFToken()).build());
            get.addHeader(Header.acceptAll);
            get.addHeader(Header.xCsrfToken, account.getCSRFToken());
            get.addHeader(Header.xreqXML);
            get.addHeader(HttpHeaders.REFERER, "https://freebitco.in/?op=home");
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String body = EntityUtils.toString(entity);
                    System.out.println(body);
                } else {
                    System.out.println("error: Empty body");
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw e;
        }
    }

    public static class LoginError extends Exception {
        LoginError(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class RollError extends Exception {
        RollError(String errorMessage) {
            super(errorMessage);
        }
    }

}