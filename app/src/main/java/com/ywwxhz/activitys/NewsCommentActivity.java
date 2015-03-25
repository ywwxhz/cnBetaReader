package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processers.NewsCommentProcesserImpl;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:52.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    private NewsCommentProcesserImpl mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mService = new NewsCommentProcesserImpl(this,helper);
        mService.loadData(true);
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
