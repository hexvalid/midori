package com.midori.ui;


import com.midori.bot.Account;
import com.midori.bot.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;

public class Tools {

    static void UpdateProxyCredentials(String username, String password) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    static boolean CheckProxy(String address) {
        //todo: need timeout
        Log.Print(Log.t.INF, "Checking proxy...");
        if (Utils.CheckProxyTypo(address)) {
            HttpClientContext httpClientContext = HttpClientContext.create();
            HttpClientBuilder builder = HttpClients.custom();

            if (address.startsWith("socks5://")) {
                builder.setConnectionManager(new PoolingHttpClientConnectionManager(
                        RegistryBuilder.<ConnectionSocketFactory>create()
                                .register("http", PlainConnectionSocketFactory.INSTANCE)
                                .register("https", new Account.socksSocket(SSLContexts.createSystemDefault()))
                                .build()));
                String[] parts = StringUtils.split(address.split("://")[1], ':');
                httpClientContext.setAttribute("socks.address",
                        new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
            } else if (address.startsWith("http://")) {
                String[] parts = StringUtils.split(address.split("://")[1], ':');
                builder.setProxy(new HttpHost(parts[0], Integer.parseInt(parts[1]), "http"));
            }


            builder.setConnectionManager(new PoolingHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", new Account.socksSocket(SSLContexts.createSystemDefault()))
                            .build()));
            builder.setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(10 * 1000)
                    .setConnectionRequestTimeout(10 * 1000)
                    .setSocketTimeout(10 * 1000).build());
            builder.disableAutomaticRetries();

            CloseableHttpClient httpClient = builder.build();
            HttpGet get = new HttpGet("https://httpbin.org/ip");
            long startTime = System.currentTimeMillis();
            try (CloseableHttpResponse response = httpClient.execute(get, httpClientContext)) {
                if (response.getStatusLine().getStatusCode() != 200)
                    throw new RuntimeException("Unexcepted status code"); //todo ban check
                long elapsedTime = System.currentTimeMillis() - startTime;
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String body = EntityUtils.toString(entity);
                    Log.Print(Log.t.DBG, "Proxy Ping: " + elapsedTime + "ms, Origin: " + StringUtils.substringBetween(body, ": \"", ","));
                    Log.Print(Log.t.SCS, "Proxy is working");
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.Print(Log.t.ERR, "Proxy Error: " + e.getMessage());
                return false;
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    Log.Print(Log.t.ERR, "Proxy Error: " + e.getMessage());
                }
            }
        } else {
            Log.Print(Log.t.ERR, "Proxy Error: " + "Invalid address");
            return false;
        }
    }

}
