package com.ywwxhz.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.service.NewsDetailService;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NewsDetailActivity extends ExtendBaseActivity {

    private NewsDetailService mService;

    @Override
    protected void createView(Bundle savedInstanceState) {
        mService = new NewsDetailService(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.getWebView().destroy();
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mService.getWebView().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.getWebView().onResume();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        mService.fixPadding();
    }

    @Override
    protected boolean shouldFixPose() {
        return true;
    }

    @Override
    protected View getInsertView() {
        return mService.getInsertView();
    }

    @Override
    protected void onConfigurationChangedNew(Configuration newConfig) {
        mService.fixPadding();
    }
}
