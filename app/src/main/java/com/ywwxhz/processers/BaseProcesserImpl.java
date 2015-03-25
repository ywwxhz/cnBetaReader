package com.ywwxhz.processers;

import android.content.res.Configuration;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public abstract class BaseProcesserImpl implements BaseProcesser {
    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void loadData(boolean startup) {
    }
}
