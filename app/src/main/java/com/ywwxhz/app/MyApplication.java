package com.ywwxhz.app;

import android.app.Application;
import android.os.Environment;

import com.ywwxhz.lib.kits.FileCacheKit;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileCacheKit.getInstance(getExternalCacheDir());
        } else {
            FileCacheKit.getInstance(this);
        }
    }
}
