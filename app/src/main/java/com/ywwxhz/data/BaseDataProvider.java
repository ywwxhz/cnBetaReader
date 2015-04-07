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

    public void setCallback(DataProviderCallback<T> callback) {
        this.callback = callback;
    }
    public Activity getActivity() {
        return mActivity;
    }
    public void setActivity(Activity activity){this.mActivity = activity;}

    public abstract void loadData(boolean startup);
}
