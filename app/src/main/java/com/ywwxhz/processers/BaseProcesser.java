package com.ywwxhz.processers;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ywwxhz.data.BaseDataProvider;

/**
 * CnbetaReader
 * com.ywwxhz.lib.handler
 * Created by 远望の无限(ywwxhz) on 2015/3/18 15:45.
 */
public interface BaseProcesser<E,DataProvider extends BaseDataProvider<E>> {

    void onResume();
    void onPause();
    void onDestroy();
    void assumeView(View view);
    void loadData(boolean startup);
    AppCompatActivity getActivity();
    void setProvider(DataProvider provider);
    void setActivity(AppCompatActivity activity);
    boolean onOptionsItemSelected(MenuItem item);
    void onConfigurationChanged(Configuration newConfig);
    void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
}
