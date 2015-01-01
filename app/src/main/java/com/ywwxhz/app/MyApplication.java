package com.ywwxhz.app;

import android.app.Application;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.PrefKit;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static Picasso getPicasso() {
        return picasso;
    }

    private static Picasso picasso;
    private Boolean debug;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        debug = PrefKit.getBoolean(this, R.string.pref_debug_key, false);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileCacheKit.getInstance(getExternalCacheDir());
        } else {
            FileCacheKit.getInstance(this);
        }
        MyCrashHandler.getInstance().init(this);
        picasso = Picasso.with(this);
        picasso.setLoggingEnabled(debug);
        picasso.setIndicatorsEnabled(debug);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public Boolean getDebug() {
        return debug;
    }
}
