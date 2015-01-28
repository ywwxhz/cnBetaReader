package com.ywwxhz;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ywwxhz.app.MyCrashHandler;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.PrefKit;

import java.io.File;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

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
        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache((int)Runtime.getRuntime().maxMemory() / 8))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public Boolean getDebug() {
        return debug;
    }

    @Override
    public File getCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return getExternalCacheDir();
        } else {
            return super.getCacheDir();
        }
    }
}
