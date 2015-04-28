package com.ywwxhz.activitys;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.processers.BaseProcesserImpl;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:52.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    public static final String TITLE_KEY = "key_title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (!bundle.containsKey(NewsCommentFragment.SN_KEY) || !bundle.containsKey(TITLE_KEY)
                || !bundle.containsKey(NewsCommentFragment.SID_KEY)) {
            Toast.makeText(this, "缺失token", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        setTitle("评论：" + bundle.getString(TITLE_KEY));
        NewsCommentFragment fragment = NewsCommentFragment.getInstance(bundle.getInt(NewsCommentFragment.SID_KEY)
                , bundle.getString(NewsCommentFragment.SN_KEY));
        fragment.setMenuCallBack(new BaseProcesserImpl.onOptionMenuSelect() {
            @Override
            public boolean onMenuSelect(MenuItem item) {
                if (item.getItemId() == android.R.id.home) {
                    finish();
                    return true;
                }
                return false;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
