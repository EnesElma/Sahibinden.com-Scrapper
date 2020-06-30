package com.iu.house.commons;

import java.util.Random;

public class IPGenerator {              //ip değiştirme sınıfı
    public static String getNewIP() {
        Random r = new Random();
        return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
    }
}
