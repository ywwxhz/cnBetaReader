package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.NewsDetailProcesserImpl;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NewsDetailActivity extends ExtendBaseActivity {

    private NewsDetailProcesserImpl mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = new NewsDetailProcesserImpl(this,helper);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_share:
                mService.shareAction();
                break;
            case R.id.menu_view_in_browser:
                mService.viewInBrowser();
                break;
            case R.id.menu_reflush:
                mService.makeRequest();
                break;
            case R.id.menu_font_size:
                mService.handleFontSize();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mService.onConfigurationChanged(newConfig);
    }
}
