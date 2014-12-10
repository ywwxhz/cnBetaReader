package com.ywwxhz.app;

import android.app.Application;
import android.os.Environment;

import com.ywwxhz.lib.kits.FileCacheKit;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileCacheKit.getInstance(getExternalFilesDir("cache"));
        } else {
            FileCacheKit.getInstance(this);
        }
        MyCrashHandler.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
