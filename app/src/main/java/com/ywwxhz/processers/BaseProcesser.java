package com.ywwxhz.processers;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

    boolean onOptionsItemSelected(MenuItem item);
    void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
}
