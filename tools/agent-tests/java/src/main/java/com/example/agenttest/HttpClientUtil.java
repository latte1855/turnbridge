package com.example.agenttest;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 簡易 HTTP 客戶端工具（繁中註解）。
 */
public final class HttpClientUtil {
    private HttpClientUtil() {}

    private static String sha256Hex(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                digest.update(buf, 0, len);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : digest.digest()) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static HttpResponse uploadCsv(File csv, String token, String api) throws Exception {
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(30_000)
                        .setConnectTimeout(30_000)
                        .setResponseTimeout(60_000)
                        .build())
                .build()) {

            HttpPost post = new HttpPost(api);
            post.setHeader("Authorization", "Bearer " + token);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .addPart("file", new FileBody(csv))
                    .addTextBody("sha256", sha256Hex(csv), StandardCharsets.UTF_8)
                    .addTextBody("encoding", "UTF-8")
                    .addTextBody("profile", "default");
            post.setEntity(builder.build());
            return client.execute(post);
        }
    }
}
