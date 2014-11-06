package com.ywwxhz.lib.kits;

import com.google.gson.Gson;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class Toolkit {
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
