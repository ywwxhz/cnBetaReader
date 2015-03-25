package com.ywwxhz.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.NewsListProcesserImpl;


public class NewsListActivity extends BaseToolBarActivity {

    private NewsListProcesserImpl mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mService = new NewsListProcesserImpl(this,helper);
        mService.loadData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.onResume();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mService.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mService.onReturn(requestCode, resultCode);
    }
}
