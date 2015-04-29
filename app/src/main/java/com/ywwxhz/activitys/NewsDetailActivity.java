package com.ywwxhz.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.fragments.NewsDetailFragment;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity implements NewsDetailFragment.NewsDetailCallBack {

    private NewsDetailFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&bundle.containsKey(NewsDetailFragment.NEWS_SID_KEY)&&bundle.containsKey(NewsDetailFragment.NEWS_TITLE_KEY)) {
            String title = bundle.getString(NewsDetailFragment.NEWS_TITLE_KEY);
            setTitle("详情：" + title);
            fragment = NewsDetailFragment.getInstance(bundle.getInt(NewsDetailFragment.NEWS_SID_KEY),title);
            getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
        } else {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return fragment.onKeyDown(keyCode,event)||super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public void onNewsLoadFinish(NewsItem item,boolean success) {

    }

    @Override
    public void CommentAction(int sid, String sn, String title) {
        Intent intent = new Intent(this, NewsCommentActivity.class);
        intent.putExtra(NewsCommentFragment.SN_KEY, sn);
        intent.putExtra(NewsCommentFragment.SID_KEY, sid);
        intent.putExtra(NewsCommentActivity.TITLE_KEY, title);
        startActivity(intent);
    }

    @Override
    public void onShowVideo(boolean showVideo) {

    }
}
