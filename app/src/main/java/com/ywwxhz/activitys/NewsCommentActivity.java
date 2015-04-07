package com.ywwxhz.activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.NewsCommentProvider;
import com.ywwxhz.processers.NewsCommentProcesser;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:52.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    public static final String SN_KEY = "key_sn";
    public static final String SID_KEY = "key_sid";
    public static final String TITLE_KEY = "key_title";
    private NewsCommentProcesser processer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        Bundle bundle = getIntent().getExtras();
        if (!bundle.containsKey(SN_KEY) || !bundle.containsKey(TITLE_KEY) || !bundle.containsKey(SID_KEY)) {
            Toast.makeText(this, "缺失token", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        setTitle("评论：" + bundle.getString(TITLE_KEY));
        processer = new NewsCommentProcesser(new NewsCommentProvider(this), bundle.getInt(SID_KEY), bundle.getString(SN_KEY));
        processer.setActivity(this);
        processer.assumeView(findViewById(R.id.content));
        processer.loadData(true);
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
        processer.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        processer.onCreateOptionsMenu(menu,getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }
}
