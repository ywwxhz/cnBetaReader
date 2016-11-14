package com.ywwxhz.data;

import android.app.Activity;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/27 10:12.
 */
public abstract class BaseDataProvider<T> {

    private Activity mActivity;
    protected DataProviderCallback<T> callback;

    public BaseDataProvider(Activity activity) {
        mActivity = activity;
    }

    /**
     * 设置回调
     * @param callback
     */
    public void setCallback(DataProviderCallback<T> callback) {
        this.callback = callback;
    }

    /**
     * 获取Activity
     * @return
     */
    public Activity getActivity() {
        return mActivity;
    }

    /**
     * 设置 Activity
     * @param activity
     */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 加载数据操作
     * @param startup 是否第一次加载
     */
    public abstract void loadData(boolean startup);
}
