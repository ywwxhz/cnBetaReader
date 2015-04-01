package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.NewsDetailProcesserImpl;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity {

    private NewsDetailProcesserImpl mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = new NewsDetailProcesserImpl(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.onDestroy();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mService.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mService.onKeyDown(keyCode, event)||super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        mService.onCreateOptionsMenu(menu,getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mService.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mService.onConfigurationChanged(newConfig);
    }
}
