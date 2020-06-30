package com.iu.house.commons;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

//Sertifikasyon hataları için kullanılan class
//link: https://stackoverflow.com/a/58325184

public class SSLHelper {

    static public Connection getConnection(String url){
        return Jsoup.connect(url).sslSocketFactory(SSLHelper.socketFactory());
    }

    static private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }
}