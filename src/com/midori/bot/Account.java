package com.midori.bot;

import com.midori.ui.Log;
import javafx.beans.property.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.sql.Date;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Account {
    private final IntegerProperty id;
    private final StringProperty email;
    public String password;
    public String btcAddress;
    private final DoubleProperty balance;
    private final IntegerProperty rewardPoints;
    private final IntegerProperty referrer;
    public Date lastFPDate;
    public String fingerprint;
    public Long fingerprint2;
    public int timeOffset;
    public Date loginDate;
    public Date signUpDate;
    public String signUpIP;
    private final IntegerProperty fpPlayed;
    private final DoubleProperty fpBonusReqCompleted;
    public int fpRpCost;
    public boolean emailConfirmed;
    public String stats;
    public boolean disableLottery;
    public boolean tfaEnabled;
    public String tfaSecret;
    public boolean useRPForFP;
    public String headerUserAgent;
    public String headerAcceptLanguage;
    public Date lastV3Date;
    public String fpLatencyTimes;
    private final StringProperty boosts;
    private final StringProperty proxy; //for disable: 'OFF', for debug: 'DEBUG'.
    public String logs;

    private final StringProperty _fpStatus;
    private final StringProperty _status;


    //initials
    CloseableHttpClient httpClient;
    HttpClientContext httpClientContext;
    public BasicCookieStore cookieStore;

    private ExecutorService executor;
    public boolean isExecutorRunning;


    //temp

    String socketPassword;
    String tokenName;
    String secretTokenName;
    String secretToken;
    String token;
    String fpToken;
    int fpAvailable;
    int captchaType;
    int multiAcctSameIP;
    int show2FAMsg;


    // For Database
    public Account() {
        this.id = new SimpleIntegerProperty();
        this.email = new SimpleStringProperty();
        this.balance = new SimpleDoubleProperty();
        this.rewardPoints = new SimpleIntegerProperty();
        this.referrer = new SimpleIntegerProperty();
        this.fpPlayed = new SimpleIntegerProperty();
        this.fpBonusReqCompleted = new SimpleDoubleProperty();
        this.boosts = new SimpleStringProperty();
        this.proxy = new SimpleStringProperty();
        this._fpStatus = new SimpleStringProperty();
        this._status = new SimpleStringProperty("Idle");
    }

    // For from UI (Exiting Account)
    public Account(String email, String password, int referrer, boolean tfaEnabled, String tfaSecret, int timeOffset,
                   String headerUserAgent, String headerAcceptLanguage, String proxy) {
        this();
        this.email.set(email);
        this.password = password;
        this.referrer.set(referrer);
        this.tfaEnabled = tfaEnabled;
        this.tfaSecret = tfaSecret;
        this.timeOffset = timeOffset;
        this.headerUserAgent = headerUserAgent;
        this.headerAcceptLanguage = headerAcceptLanguage;
        this.proxy.set(proxy);
        this.fingerprint = RandomStringUtils.random(32, Rune.baseBytes);
        this.fingerprint2 = RandomUtils.nextLong(1111111111L, 9999999999L);
        this.cookieStore = new BasicCookieStore();
    }

    public void openHTTPClient() {
        Log.Print(Log.t.DBG, "Openning HTTP Client...");
        this.httpClientContext = HttpClientContext.create();
        HttpClientBuilder builder = HttpClients.custom();
        builder.setDefaultCookieStore(this.cookieStore)
                .setUserAgent(this.headerUserAgent)
                .setDefaultHeaders(Collections.singletonList(
                        new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, headerAcceptLanguage)))
                .disableAutomaticRetries()
                .disableConnectionState()
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(15 * 1000).build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(15 * 1000)
                        .setConnectionRequestTimeout(15 * 1000)
                        .setSocketTimeout(15 * 1000).build());

        if (this.proxy.get().equals("DEBUG")) {
            builder.setProxy(new HttpHost("127.0.0.1", 8080, "http")); //should be final variable
            try {
                builder.setSSLContext(SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build());
            } catch (Exception ignored) {
                //ignored error for debug
            }
        } else if (Utils.CheckProxyTypo(this.proxy.get())) {

            if (this.proxy.get().startsWith("socks5://")) {
                builder.setConnectionManager(new PoolingHttpClientConnectionManager(
                        RegistryBuilder.<ConnectionSocketFactory>create()
                                .register("http", PlainConnectionSocketFactory.INSTANCE)
                                .register("https", new socksSocket(SSLContexts.createSystemDefault()))
                                .build()));
                String[] parts = StringUtils.split(this.proxy.get().split("://")[1], ':');
                httpClientContext.setAttribute("socks.address",
                        new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
            } else if (this.proxy.get().startsWith("http://")) {
                String[] parts = StringUtils.split(this.proxy.get().split("://")[1], ':');
                builder.setProxy(new HttpHost(parts[0], Integer.parseInt(parts[1]), "http"));
            }
        }
        this.httpClient = builder.build();
    }

    public void closeHTTPClient() throws IOException {
        Log.Print(Log.t.DBG, "Closing HTTP Client...");
        this.httpClient.close();
        this.httpClientContext = null;
        this.httpClient = null;
    }

    public IntegerProperty idProperty() {
        return this.id;
    }

    public StringProperty emailProperty() {
        return this.email;
    }

    public DoubleProperty balanceProperty() {
        return this.balance;
    }

    public IntegerProperty rewardPointsProperty() {
        return this.rewardPoints;
    }

    public IntegerProperty referrerProperty() {
        return this.referrer;
    }

    public IntegerProperty fpPlayedProperty() {
        return this.fpPlayed;
    }

    public DoubleProperty fpBonusReqCompletedProperty() {
        return this.fpBonusReqCompleted;
    }

    public StringProperty boostsProperty() {
        return this.boosts;
    }

    public StringProperty proxyProperty() {
        return this.proxy;
    }

    public StringProperty _fpStatusProperty() {
        return this._fpStatus;
    }

    public StringProperty _statusProperty() {
        return this._status;
    }


    public void setID(int id) {
        this.id.set(id);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints.set(rewardPoints);
    }

    public void setReferrer(int referrer) {
        this.referrer.set(referrer);
    }

    public void setFpPlayed(int fpPlayed) {
        this.fpPlayed.set(fpPlayed);
    }

    public void setFpBonusReqCompleted(double fpBonusReqCompleted) {
        this.fpBonusReqCompleted.set(fpBonusReqCompleted);
    }

    public void setBoosts(String boosts) {
        if (boosts != null && boosts.length() > 3) {
            this.boosts.set(boosts);
        } else {
            this.boosts.set("");
        }
    }

    public void setProxy(String proxy) {
        this.proxy.set(proxy);
    }

    public void set_FPStatus(String _fpStatus) {
        this._fpStatus.set(_fpStatus);
    }

    public void set_Status(String _status) {
        this._status.set(_status);
    }

    public int getRewardPoints() {
        return this.rewardPoints.get();
    }


    public String getBoosts() {
        return this.boosts.get();
    }

    private String getCookie(String name) {
        for (Cookie cookie : this.cookieStore.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public void addCookie(String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain("freebitco.in");
        cookie.setPath("/");
        this.cookieStore.addCookie(cookie);
    }

    String getCSRFToken() {
        return this.getCookie("csrf_token");
    }


    void setloginCredentials(String cookiePassword) {
        addCookie("btc_address", this.btcAddress);
        addCookie("password", cookiePassword);
        addCookie("have_account", "1");
        this.loginDate = new Date(System.currentTimeMillis());
    }

    public String logDomain() {
        return "(" + id.get() + "): ";
    }


    public static class socksSocket extends SSLConnectionSocketFactory {
        public socksSocket(final SSLContext sslContext) {
            super(sslContext);
        }

        @Override
        public Socket createSocket(final HttpContext context) {
            InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
            return new Socket(proxy);
        }
    }

    public boolean isReadyForRoll() {
        return DateUtils.addHours(this.lastFPDate, 1).before(new java.util.Date());
    }

    public long remaingMillisForRoll() {
        java.util.Date now = new java.util.Date();
        long time = now.getTime() - DateUtils.addHours(this.lastFPDate, 1).getTime();
        if (time > 0) {
            return 0;
        } else {
            return Math.abs(now.getTime() - DateUtils.addHours(this.lastFPDate, 1).getTime());
        }
    }

    public void startExecuter() {
        if (this.executor == null || this.executor.isTerminated()) {
            this.executor = Executors.newSingleThreadExecutor();
            this.executor.submit(new Tasks.FPTask(this));
            this.isExecutorRunning = true;
        }

    }


    public void stopExecuter() {
        if (this.executor != null) {
            this.executor.shutdown();
            this.executor.shutdownNow();
            this.isExecutorRunning = false;
        }
    }
}

