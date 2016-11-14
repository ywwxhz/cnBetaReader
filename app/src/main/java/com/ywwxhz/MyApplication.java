package com.ywwxhz;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.model.HttpHeaders;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.crash.CustomActivityOnCrash;
import com.ywwxhz.lib.Emoticons;
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
    private boolean listImageShowStatusChange;

    public DbUtils getDbUtils() {
        return mDbUtils;
    }

    private DbUtils mDbUtils;

    public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).build();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        debug = PrefKit.getBoolean(this, R.string.pref_debug_key, false);
        initOKHttpClient();
        FileCacheKit.getInstance(this);
        CustomActivityOnCrash.install(this);
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
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
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

    public File getInternalCacheDir() {
        return super.getCacheDir();
    }

    public static DisplayImageOptions getDefaultDisplayOption() {
        return options;
    }

    public boolean isListImageShowStatusChange() {
        return listImageShowStatusChange;
    }

    public void setListImageShowStatusChange(boolean listImageShowStatusChange) {
        this.listImageShowStatusChange = listImageShowStatusChange;
    }

    public String getUpdateUrl() {
        return "http://ywwxhz.byethost31.com/projects/cnBetaPlus/api/update-" +
                PrefKit.getString(this, R.string.pref_release_channel_key, BuildConfig.BUILD_TYPE) + ".php?ckattempt=1";
    }

    public void initOKHttpClient(){
        HttpHeaders headers = new HttpHeaders();
        headers.put("Referer", "http://www.cnbeta.com/");
        headers.put("Origin", "http://www.cnbeta.com");
        headers.put("X-Requested-With", "XMLHttpRequest");
        //必须调用初始化
        OkGo.init(this);
        //以下都不是必须的，根据需要自行选择
        OkGo.getInstance()//
                .debug("cnBeta Plus")                  //是否打开调试
                .setCacheMode(CacheMode.NO_CACHE)
                .setCookieStore(new MemoryCookieStore())
                .setConnectTimeout(3000)               //全局的连接超时时间
                .setReadTimeOut(6000)                  //全局的读取超时时间
                .setWriteTimeOut(6000)                 //全局的写入超时时间
                .addCommonHeaders(headers);            //设置全局公共头
    }
}
