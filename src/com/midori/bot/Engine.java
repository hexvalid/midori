package com.midori.bot;

import com.midori.database.DBAccTools;
import com.midori.ui.Log;
import com.sun.istack.internal.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Engine {

    public static class URL {
        final public static String home = "https://freebitco.in/?op=home";
        final public static String root = "https://freebitco.in/";
        final private static String signUp = "https://freebitco.in/?op=signup_page";
        final private static String statsNewPrivate = "https://freebitco.in/stats_new_private/";
        final private static String api = "https://freebitco.in/cgi-bin/api.pl";
        final private static String fpCheck = "https://freebitco.in/cgi-bin/fp_check.pl";
        final private static String botdetect = "https://captchas.freebitco.in/botdetect/e/live/index.php";
        final private static String enableTFA = "https://freebitco.in/?op=home&tab=edit&tab2=enable_2fa";
    }

    private static class Header {
        final private static BasicHeader acceptDefault = new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        final private static BasicHeader acceptAll = new BasicHeader(HttpHeaders.ACCEPT, "*/*");
        final private static BasicHeader acceptImage = new BasicHeader(HttpHeaders.ACCEPT, "image/webp,*/*");
        final private static BasicHeader basicOrigin = new BasicHeader("Origin", "https://freebitco.in");
        final private static BasicHeader xreqXML = new BasicHeader("X-Requested-With", "XMLHttpRequest");
        final private static String xCsrfToken = "X-Csrf-Token";
    }


    public static ReentrantLock LOCK = new ReentrantLock();


    public static void Login(Account account, boolean signup) throws IOException, LoginError, URISyntaxException, InterruptedException {
        Log.Print(Log.t.DBG, account.logDomain() + "Loading initial page for login...");
        try {
            int cid = 0;
            account.openHTTPClient();
            HttpGet get = new HttpGet(URL.signUp);
            get.addHeader(Header.acceptDefault);
            CloseableHttpResponse mainResponse = account.httpClient.execute(get, account.httpClientContext);
            String mainBody = EntityUtils.toString(mainResponse.getEntity());
            mainResponse.close();
            HttpPost post = new HttpPost(URL.root);
            post.addHeader(Header.acceptAll);
            post.addHeader(Header.basicOrigin);
            post.addHeader(HttpHeaders.REFERER, URL.signUp);
            post.addHeader(Header.xCsrfToken, account.getCSRFToken());
            post.addHeader(Header.xreqXML);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", account.getCSRFToken()));
            if (signup) {
                Log.Print(Log.t.DBG, account.logDomain() + "Signing up...");
                params.add(new BasicNameValuePair("op", "signup_new"));
                params.add(new BasicNameValuePair("email", account.emailProperty().get()));
                params.add(new BasicNameValuePair("fingerprint", account.fingerprint));
                params.add(new BasicNameValuePair("referrer", String.valueOf(account.referrerProperty().get())));
                //@parseller:0
                params.add(new BasicNameValuePair("token", StringUtils.substringBetween(mainBody, "signup_token = '", "'")));
                Captcha.Response cr = solveBotdetectCaptcha(account, 1);
                cid = cr.taskId;
                params.addAll(cr.params);
                account.signUpDate = new java.sql.Date(System.currentTimeMillis());
            } else {
                Log.Print(Log.t.DBG, account.logDomain() + "Logging in...");
                params.add(new BasicNameValuePair("op", "login_new"));
                params.add(new BasicNameValuePair("btc_address", account.emailProperty().get()));
                params.add(new BasicNameValuePair("tfa_code", "")); //todo: support tfa code
            }
            params.add(new BasicNameValuePair("password", account.password));
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
                    } else if (body.charAt(0) == 'e') {
                        if (body.contains("Captcha is incorrect")) {
                            Captcha.reportIncorrectImageCaptcha(cid);
                            throw new LoginError("Captcha is incorrect");
                        }
                        throw new LoginError(body.substring(body.lastIndexOf(':') + 1));
                    } else throw new LoginError("Unexcepted response");
                } else {
                    throw new LoginError("Empty body");
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static void Home(Account account, @Nullable String urlSuffix, String refferer) throws IOException, InterruptedException {
        if (urlSuffix == null) {
            Log.Print(Log.t.DBG, account.logDomain() + "Loading home...");
            urlSuffix = "";
        } else {
            Log.Print(Log.t.DBG, account.logDomain() + "Loading special home...");
        }
        try {
            account.openHTTPClient();
            HttpGet get1 = new HttpGet(URL.root + urlSuffix);
            get1.addHeader(Header.acceptDefault);
            get1.addHeader(HttpHeaders.REFERER, refferer);
            try (CloseableHttpResponse response = account.httpClient.execute(get1, account.httpClientContext)) { //todo: err connection reset
                if (response.getStatusLine().getStatusCode() != 200)
                    throw new RuntimeException("Unexcepted status code: " + response.getStatusLine().getStatusCode()); //todo ban check
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

                    //@parseller:12
                    account.emailConfirmed = body.contains("<div style=\"display:none;\"> <p style=\"text-transform: none;\">You need to verify your email address");

                    //@parseller:13
                    account.tfaEnabled = !body.contains("<div style=\"display:none;\"> <p style=\"text-transform: none;\">Please enter the code generated");

                }
            }

            //MultiThreading Now
            ExecutorService pool = Executors.newFixedThreadPool(6);
            CountDownLatch latch = new CountDownLatch(6);
            JSONObject stats = new JSONObject();


            pool.execute(() -> {
                Log.Print(Log.t.DBG, account.logDomain() + "Loading initial stats...");
                try {
                    HttpGet get = new HttpGet(URL.statsNewPrivate);
                    get.setURI(new URIBuilder(get.getURI())
                            .addParameter("u", String.valueOf(account.idProperty().get()))
                            .addParameter("p", account.socketPassword)
                            .addParameter("f", "user_stats_initial")
                            .addParameter("csrf_token", account.getCSRFToken()).build());
                    get.addHeader(Header.acceptAll);
                    get.addHeader(HttpHeaders.REFERER, URL.root);
                    get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                    get.addHeader(Header.xreqXML);
                    try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            //@parseller:j1
                            stats.put("user_stats_initial", new JSONObject(EntityUtils.toString(entity)));
                            account.setFpPlayed(stats.getJSONObject("user_stats_initial")
                                    .getJSONObject("user").getInt("free_spins_played"));
                        }
                    }
                } catch (URISyntaxException | IOException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });


            pool.execute(() -> {
                Log.Print(Log.t.DBG, account.logDomain() + "Recording fingerprint...");
                try {
                    HttpGet get = new HttpGet(URL.api);
                    get.setURI(new URIBuilder(get.getURI())
                            .addParameter("op", "record_fingerprint")
                            .addParameter("fingerprint", account.fingerprint)
                            .addParameter("csrf_token", account.getCSRFToken()).build());
                    get.addHeader(Header.acceptAll);
                    get.addHeader(HttpHeaders.REFERER, URL.root);
                    get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                    get.addHeader(Header.xreqXML);
                    account.httpClient.execute(get, account.httpClientContext).close();
                } catch (URISyntaxException | IOException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            pool.execute(() -> {
                Log.Print(Log.t.DBG, account.logDomain() + "Recording time offset...");
                try {
                    HttpGet get = new HttpGet(URL.api);
                    get.setURI(new URIBuilder(get.getURI())
                            .addParameter("op", "record_user_data")
                            .addParameter("type", "time_offset")
                            .addParameter("value", String.valueOf(account.timeOffset))
                            .addParameter("csrf_token", account.getCSRFToken()).build());
                    get.addHeader(Header.acceptAll);
                    get.addHeader(HttpHeaders.REFERER, URL.root);
                    get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                    get.addHeader(Header.xreqXML);
                    account.httpClient.execute(get, account.httpClientContext).close();
                } catch (URISyntaxException | IOException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            pool.execute(() -> {
                Log.Print(Log.t.DBG, account.logDomain() + "Getting FP Token...");
                try {
                    HttpGet get = new HttpGet(URL.fpCheck);
                    get.setURI(new URIBuilder(get.getURI())
                            .addParameter("s", account.secretTokenName)
                            .addParameter("csrf_token", account.getCSRFToken()).build());
                    get.addHeader(Header.acceptAll);
                    get.addHeader(HttpHeaders.REFERER, URL.root);
                    get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                    get.addHeader(Header.xreqXML);
                    try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            account.fpToken = EntityUtils.toString(entity);
                        }
                    }
                } catch (URISyntaxException | IOException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            pool.execute(() -> {
                Log.Print(Log.t.DBG, account.logDomain() + "Loading stats...");
                try {
                    HttpGet get = new HttpGet(URL.statsNewPrivate);
                    get.setURI(new URIBuilder(get.getURI())
                            .addParameter("u", String.valueOf(account.idProperty().get()))
                            .addParameter("p", account.socketPassword)
                            .addParameter("f", "user_stats")
                            .addParameter("csrf_token", account.getCSRFToken()).build());
                    get.addHeader(Header.acceptAll);
                    get.addHeader(HttpHeaders.REFERER, URL.root);
                    get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                    get.addHeader(Header.xreqXML);
                    try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            //@parseller:j2
                            stats.put("user_stats", new JSONObject(EntityUtils.toString(entity)));
                        }
                    }
                } catch (URISyntaxException | IOException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });


            pool.execute(() -> {
                try {
                    if (account.captchaType == 1 || account.lastV3Date == null
                            || DateUtils.addMinutes(account.lastV3Date, Prefs.RECAPTCHA_V3_TIME_MINUTES).before(new java.util.Date())) {
                        RecordRecaptchaV3(account);
                        account.lastV3Date = new java.sql.Date(System.currentTimeMillis());
                    }
                } catch (URISyntaxException | IOException | InterruptedException e) {
                    Log.Print(Log.t.CRI, account.logDomain() + "Error at multithread: " + e);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            latch.await();
            pool.shutdown();

            account.stats = stats.toString();
            Log.Print(Log.t.SCS, account.logDomain() + "Account is homed");

        } finally {
            account.closeHTTPClient();
        }
    }

    public static void Roll(Account account) throws IOException, RollError, InterruptedException {
        if (!account.emailConfirmed) {
            Log.Print(Log.t.WRN, account.logDomain() + "This account have not a email confirmation. " +
                    "This roll may cause break anything!'");
        }
        try {
            AtomicInteger cid1 = new AtomicInteger();
            AtomicInteger cid2 = new AtomicInteger();
            Captcha.Response cr = null;
            account.openHTTPClient();
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
                    ExecutorService pool = Executors.newFixedThreadPool(2);
                    CountDownLatch latch = new CountDownLatch(2);
                    pool.execute(() -> {
                        try {
                            Captcha.Response cr1 = solveBotdetectCaptcha(account, 1);
                            cid1.set(cr1.taskId);
                            params.addAll(cr1.params);
                        } catch (IOException | URISyntaxException e) {
                            Log.Print(Log.t.CRI, "Roll thread error: " + e.getMessage());
                        }
                        latch.countDown();
                    });
                    pool.execute(() -> {
                        try {
                            Captcha.Response cr2 = solveBotdetectCaptcha(account, 2);
                            cid2.set(cr2.taskId);
                            params.addAll(cr2.params);
                        } catch (IOException | URISyntaxException e) {
                            Log.Print(Log.t.CRI, "Roll thread error: " + e.getMessage());
                        }
                        latch.countDown();
                    });
                    latch.await();
                    pool.shutdown();
                    params.add(new BasicNameValuePair("g_recaptcha_response", ""));
                }
            } else {
                Log.Print(Log.t.UNK, account.logDomain() + "Unknown solving type! " + account.captchaType);
                account.set_Status("Unknown solving type!");
                if (account.emailConfirmed) {
                    Thread.sleep(10000 * 60 * 1000);
                } else {
                    throw new RollError(String.valueOf(account.captchaType));
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
                        //fmay be ticket count is null. so lets fix:
                        body = StringUtils.replace(body, "::", ":0:", -1);
                        String[] vals = StringUtils.split(body, ':');
                        //s:4575:0.00000137:0.00000027:1574947894:0:215dc0aa218e2e24a34aac8aec34f19ae64b1aa7afe0d76fad34bca1178c9483:IUuSHJ01guTg7tm9:9337:756bb738d5a82ead4536403b239977c232297a121a886d701f759aa9adc0dcdc:09c39e9a3227b6bb52697f74d8aaa0f975f17deae7d6a1480ac32b2b1ea3ad2b:IUuSHJ01guTg7tm9:9336:50:19841:2:2:0.00000000:62.8423
                        //s:5771:0.00000557:0.00000027:1575226650:0:578f5d3ce84e43a03e5d2faa05013746e236031a693f7a023c635a5445391a51:ZyI4UMsJGB1Pq6Jy:8097:a2a0d2cdcbfce216ea046e758f765df764796eda04bb67bf874f1083a0fbfb58:7064fb745f2b2f57ff1c14b422af637d12c704f4e484a7900adb5ce7ba0377f9:ZyI4UMsJGB1Pq6Jy:8096::1532:0:4:0.00000000:71.5271

                        //todo: add half of vals[3] to master account
                        Log.Print(Log.t.SCS, account.logDomain() + "Rolled: " + vals[1] + ", earned " + vals[3] + ", " +
                                vals[16] + " RP, " + vals[15] + " lottery tickets");
                        account.setBalance(Double.parseDouble(vals[2]));
                        account.addCookie("last_play", vals[4]);
                        account.lastFPDate = new java.sql.Date(System.currentTimeMillis());
                        account.setRewardPoints(Integer.parseInt(vals[14]));
                        account.setFpBonusReqCompleted(Double.parseDouble(vals[18]));
                        account.setFpPlayed(account.fpPlayedProperty().get() + 1);

                        //todo: need append to log account

                        //todo: check captcha error and try again
                        //todo: check same rolled ip error and calculate wait time
                    } else if (body.charAt(0) == 'e') {
                        if (body.contains("Captcha is incorrect")) {
                            if (cr != null) {
                                Captcha.reportIncorrectImageCaptcha(cr.taskId);
                            }
                            throw new RollError("Captcha is incorrect");
                        } else if (body.contains("Someone has already played")) {
                            Thread.sleep(60 * 60 * 1000);
                        }
                        throw new RollError(body);
                    } else {
                        throw new RollError("Unexcepted response: " + body);
                    }
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static AICaptcha.BDCaptcha getBotDetectCaptcha(Account account) throws URISyntaxException {
        AICaptcha.BDCaptcha bdCaptcha = new AICaptcha.BDCaptcha();
        Log.Print(Log.t.DBG, "Getting BotDetect Captcha...");
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
                bdCaptcha.random = EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace(); //todo
        }

        HttpGet get2 = new HttpGet(URL.botdetect);
        get2.setURI(new URIBuilder(get2.getURI()).addParameter("random", bdCaptcha.random).build());
        get2.addHeader(Header.acceptImage);
        get2.addHeader(HttpHeaders.REFERER, URL.root);
        try (CloseableHttpResponse response = account.httpClient.execute(get2, account.httpClientContext)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {

                ByteArrayInputStream bais = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                bdCaptcha.image = ImageIO.read(bais);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace(); //todo
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bdCaptcha;
    }

    public static Captcha.Response solveBotdetectCaptcha(Account account, int i) throws IOException, URISyntaxException {
        Captcha.Response cr = new Captcha.Response();
        cr.params = new ArrayList<>();
        Log.Print(Log.t.DBG, "Thinking BotDetect Captcha (#" + i + ")...");
        AICaptcha.BDCaptcha bdCaptcha;
        while (true) {
            bdCaptcha = getBotDetectCaptcha(account);
            if (AICaptcha.IsTrainedCaptcha(bdCaptcha.image)) {
                break;
            }

        }

        File outputfile = new File("/tmp/detect/" + bdCaptcha.random + ".jpg");
        ImageIO.write(bdCaptcha.image, "jpg", outputfile);

        String answer = (new BufferedReader(new InputStreamReader((
                new ProcessBuilder("/usr/bin/python3", "/home/hexvalid/Videos/ai-bdc/interface.py", "/tmp/detect/" + bdCaptcha.random + ".jpg")).start().getInputStream()))).readLine();


        bdCaptcha.response = answer;
        System.out.println("answer: " + bdCaptcha.response);
        ImageIO.write(bdCaptcha.image, "jpg",
                new File("/tmp/detect/answer-" + bdCaptcha.response + ".jpg"));
        System.out.println("OK...");


        String suffix = "";
        if (i > 1) {
            suffix = String.valueOf(i);
        }
        cr.params.add(new BasicNameValuePair("botdetect_random" + suffix, bdCaptcha.random));
        cr.params.add(new BasicNameValuePair("botdetect_response" + suffix, bdCaptcha.response));

        return cr;
    }

    public static void Open(Account account, String url) throws IOException {
        try {
            account.openHTTPClient();
            Log.Print(Log.t.DBG, account.logDomain() + "Openning link with account's engine...");
            HttpGet get = new HttpGet(url);
            get.addHeader(Header.acceptDefault);
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.Print(Log.t.INF, EntityUtils.toString(entity).replaceAll("<BR>", ""));
                    //todo: err check
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static void toggleLottery(Account account) throws IOException, URISyntaxException {
        try {
            account.openHTTPClient();
            int value;
            if (account.disableLottery) {
                Log.Print(Log.t.DBG, account.logDomain() + "Enabling lottery...");
                value = 0;
            } else {
                Log.Print(Log.t.DBG, account.logDomain() + "Disabling lottery...");
                value = 1;
            }
            HttpGet get = new HttpGet(URL.root);
            get.setURI(new URIBuilder(get.getURI())
                    .addParameter("op", "toggle_lottery")
                    .addParameter("value", String.valueOf(value))
                    .addParameter("csrf_token", account.getCSRFToken()).build());
            get.addHeader(Header.acceptAll);
            get.addHeader(HttpHeaders.REFERER, URL.root);
            get.addHeader(Header.xCsrfToken, account.getCSRFToken());
            get.addHeader(Header.xreqXML);
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.Print(Log.t.INF, EntityUtils.toString(entity));
                    if (value == 0) {
                        account.disableLottery = false;
                    } else {
                        account.disableLottery = true;
                    }
                    //todo: err check
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static void confirmEmail(Account account) throws IOException, URISyntaxException {
        Log.Print(Log.t.DBG, account.logDomain() + "Requesting email confirmation link...");
        try {
            account.openHTTPClient();
            HttpGet get = new HttpGet(URL.root);
            get.setURI(new URIBuilder(get.getURI())
                    .addParameter("op", "confirm_email")
                    .addParameter("csrf_token", account.getCSRFToken()).build());
            get.addHeader(Header.acceptAll);
            get.addHeader(HttpHeaders.REFERER, URL.root);
            get.addHeader(Header.xCsrfToken, account.getCSRFToken());
            get.addHeader(Header.xreqXML);
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.Print(Log.t.INF, EntityUtils.toString(entity).replaceAll("<BR>", ""));
                    //todo: err check
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static void enableTFA(Account account) throws IOException, URISyntaxException, GeneralSecurityException, SQLException {
        Log.Print(Log.t.DBG, account.logDomain() + "Getting TFA Secret...");
        try {
            account.openHTTPClient();
            HttpGet get = new HttpGet(URL.root);
            get.setURI(new URIBuilder(get.getURI())
                    .addParameter("op", "enable_2fa")
                    .addParameter("func", "show")
                    .addParameter("csrf_token", account.getCSRFToken()).build());
            get.addHeader(Header.acceptAll);
            get.addHeader(HttpHeaders.REFERER, URL.enableTFA);
            get.addHeader(Header.xCsrfToken, account.getCSRFToken());
            get.addHeader(Header.xreqXML);
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String body = EntityUtils.toString(entity);
                    if (body.charAt(0) == 's') {
                        String[] vals = StringUtils.split(body, ':');
                        account.tfaSecret = vals[vals.length - 1];
                    }  //todo: err check

                }
            }
            Log.Print(Log.t.DBG, account.logDomain() + "Enabling TFA...");
            HttpPost post = new HttpPost(URL.root);
            post.addHeader(Header.acceptAll);
            post.addHeader(Header.basicOrigin);
            post.addHeader(HttpHeaders.REFERER, URL.enableTFA);
            post.addHeader(Header.xCsrfToken, account.getCSRFToken());
            post.addHeader(Header.xreqXML);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token", account.getCSRFToken()));
            params.add(new BasicNameValuePair("op", "enable_2fa"));
            params.add(new BasicNameValuePair("func", "enable"));
            params.add(new BasicNameValuePair("code", OTP.generatePIN(account.tfaSecret)));
            params.add(new BasicNameValuePair("phone", ""));
            post.setEntity(new UrlEncodedFormEntity(params));
            try (CloseableHttpResponse response = account.httpClient.execute(post, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.Print(Log.t.INF, EntityUtils.toString(entity));
                    DBAccTools.UpdateTFASecret(account);
                    //todo: err check
                }
            }
        } finally {
            account.closeHTTPClient();
        }
    }

    public static void RecordRecaptchaV3(Account account) throws IOException, URISyntaxException, InterruptedException {
        int taskId = Captcha.sendRecaptchaV3();
        String cr = Captcha.checkCaptcha(taskId, true);
        Log.Print(Log.t.DBG, account.logDomain() + "Sending reCAPTCHA v3 token...");
        try {
            HttpGet get = new HttpGet(URL.api);
            get.setURI(new URIBuilder(get.getURI())
                    .addParameter("op", "record_recaptcha_v3")
                    .addParameter("token", cr)
                    .addParameter("csrf_token", account.getCSRFToken()).build());
            get.addHeader(Header.acceptAll);
            get.addHeader(HttpHeaders.REFERER, URL.root);
            get.addHeader(Header.xCsrfToken, account.getCSRFToken());
            get.addHeader(Header.xreqXML);
            try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Log.Print(Log.t.DBG, account.logDomain() + "ReCaptcha v3 response: " + EntityUtils.toString(entity));
                }
            }
        } catch (RuntimeException | URISyntaxException | IOException e) {
            throw e;
        }
    }


    public static void RedeemRewards(Account account, Boost boost) throws IOException, URISyntaxException {
        Log.Print(Log.t.DBG, account.logDomain() + "Reedeeming boost: " + boost.name);
        try {
            account.openHTTPClient();
            try {
                HttpGet get = new HttpGet(URL.root);
                get.setURI(new URIBuilder(get.getURI())
                        .addParameter("op", "redeem_rewards")
                        .addParameter("id", boost.id)
                        .addParameter("points", "")
                        .addParameter("csrf_token", account.getCSRFToken()).build());
                get.addHeader(Header.acceptAll);
                get.addHeader(HttpHeaders.REFERER, URL.root);
                get.addHeader(Header.xCsrfToken, account.getCSRFToken());
                get.addHeader(Header.xreqXML);
                try (CloseableHttpResponse response = account.httpClient.execute(get, account.httpClientContext)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        Log.Print(Log.t.INF, account.logDomain() + "BOOST RESPONSE: " + EntityUtils.toString(entity));
                        //TODO: ERR CHECK
                    }
                }
            } catch (RuntimeException | URISyntaxException | IOException e) {
                throw e;
            }
        } finally {
            account.closeHTTPClient();
        }
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