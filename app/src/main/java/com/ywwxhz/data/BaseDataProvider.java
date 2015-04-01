package com.ywwxhz.data;

import android.app.Activity;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/27 10:12.
 */
public abstract class BaseDataProvider<T> {

    public BaseDataProvider(Activity activity) {
        mActivity = activity;
    }

    private Activity mActivity;
    protected DataProviderCallback<T> callback;
    public void setCallback(DataProviderCallback<T> callback) {
        this.callback = callback;
    }
    public abstract void loadData(boolean startup);
    public Activity getActivity() {
        return mActivity;
    }
}
