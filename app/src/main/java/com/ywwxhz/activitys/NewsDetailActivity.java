package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.processers.NewsDetailProcesser;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity {
    public static final String NEWS_ITEM_KEY = "key_news_item";
    private NewsDetailProcesser processer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getIntent().getExtras().containsKey(NEWS_ITEM_KEY)) {
            processer = new NewsDetailProcesser(new NewsDetailProvider(this));
            processer.setActivity(this);
            processer.assumeView(findViewById(R.id.content));
            processer.loadData(true);
        } else {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processer.onDestroy();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        processer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        processer.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return processer.onKeyDown(keyCode, event)||super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        processer.onCreateOptionsMenu(menu,getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return processer.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        processer.onConfigurationChanged(newConfig);
    }
}
