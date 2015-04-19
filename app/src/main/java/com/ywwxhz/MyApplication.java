package com.ywwxhz;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.Emoticons;
import com.ywwxhz.lib.MyCrashHandler;
import com.ywwxhz.lib.database.DbUtils;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.PrefKit;

import java.io.File;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    private Boolean debug;

    public static FileNameGenerator getFileNameGenerator() {
        return fileNameGenerator;
    }

    private static FileNameGenerator fileNameGenerator = new Md5FileNameGenerator();

    public DbUtils getDbUtils() {
        return mDbUtils;
    }

    private DbUtils mDbUtils;

    @Override
    public void onCreate() {
        instance = this;
        debug = PrefKit.getBoolean(this, R.string.pref_debug_key, false);
        FileCacheKit.getInstance(this);
        MyCrashHandler.getInstance().init(this);
        initImageLoader(getApplicationContext());
        Emoticons.init(this);
        mDbUtils = DbUtils.create(this);
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(fileNameGenerator)
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO);
        if (debug) {
            builder.writeDebugLogs();
        }
        ImageLoaderConfiguration config = builder.build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public boolean getDebug() {
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
