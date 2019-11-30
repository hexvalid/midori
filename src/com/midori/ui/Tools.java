package com.midori.ui;


import com.midori.bot.Account;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
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
        Log.Print(Log.t.INF, "Checking proxy...");
        if (StringUtils.countMatches(address, '.') == 3 && StringUtils.countMatches(address, ':') == 1) {
            HttpClientContext httpClientContext = HttpClientContext.create();
            HttpClientBuilder builder = HttpClients.custom();
            builder.setConnectionManager(new PoolingHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", new Account.socksSocket(SSLContexts.createSystemDefault()))
                            .build()));

            String[] parts = StringUtils.split(address, ':');
            httpClientContext.setAttribute("socks.address",
                    new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
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
