package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.NewsCommentProcesserImpl;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    private NewsCommentProcesserImpl mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mService = new NewsCommentProcesserImpl(this,helper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mService.onConfigurationChanged(newConfig);
    }
}
