package com.ywwxhz.processers;

import android.content.Context;
import android.content.res.Configuration;

/**
 * CnbetaReader
 * com.ywwxhz.lib.handler
 * Created by 远望の无限(ywwxhz) on 2015/3/18 15:45.
 */
public interface BaseProcesser {
    Context getContext();
    void onResume();
    void onConfigurationChanged(Configuration newConfig);
    void onPause();
    void onDestroy();
    void loadData(boolean startup);
}
