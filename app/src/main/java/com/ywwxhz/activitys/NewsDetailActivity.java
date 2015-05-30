package com.ywwxhz.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.processers.BaseProcesserImpl;
import com.ywwxhz.widget.FixViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity implements NewsDetailFragment.NewsDetailCallBack {

    private List<Fragment> fragments = new ArrayList<>(2);
    private FixViewPager pager;
    private FragmentAdapter adapter;
    private String title;
    private boolean isNewDesignMode;
    private ViewGroup contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        isNewDesignMode = PrefKit.getBoolean(this, R.string.pref_new_detail_key, true);
        if (bundle != null && bundle.containsKey(NewsDetailFragment.NEWS_SID_KEY) && bundle.containsKey(NewsDetailFragment.NEWS_TITLE_KEY)) {
            title = bundle.getString(NewsDetailFragment.NEWS_TITLE_KEY);
            setTitle("详情：" + title);
            contentView = (ViewGroup) findViewById(R.id.content);
            fragments.add(NewsDetailFragment.getInstance(bundle.getInt(NewsDetailFragment.NEWS_SID_KEY), title));
            if (isNewDesignMode) {
                setContentView(R.layout.pager_layout);
                pager = (FixViewPager) findViewById(R.id.pager);
                adapter = new FragmentAdapter(getSupportFragmentManager());
                pager.setAdapter(adapter);
                pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            setTitle("详情：" + title);
                            setSwipeBackEnable(PrefKit.getBoolean(NewsDetailActivity.this, R.string.pref_swipeback_key, true));
                        } else {
                            setTitle("评论：" + title);
                            setSwipeBackEnable(false);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, fragments.get(0)).commit();
            }
        } else {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isNewDesignMode && keyCode == KeyEvent.KEYCODE_BACK && pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
            return true;
        }
        return ((NewsDetailFragment) fragments.get(0)).onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public void onNewsLoadFinish(NewsItem item, boolean success) {
        if (isNewDesignMode && success && fragments.size() == 1) {
            fragments.add(NewsCommentFragment.getInstance(item.getSid(), item.getSN())
                    .setMenuCallBack(new BaseProcesserImpl.onOptionMenuSelect() {
                        @Override
                        public boolean onMenuSelect(MenuItem item) {
                            if (item.getItemId() == android.R.id.home) {
                                pager.setCurrentItem(0, true);
                                return true;
                            }
                            return false;
                        }
                    }));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void CommentAction(int sid, String sn, String title) {
        if (isNewDesignMode) {
            pager.setCurrentItem(1, true);
        } else {
            Intent intent = new Intent(this, NewsCommentActivity.class);
            intent.putExtra(NewsCommentFragment.SN_KEY, sn);
            intent.putExtra(NewsCommentFragment.SID_KEY, sid);
            intent.putExtra(NewsCommentActivity.TITLE_KEY, title);
            startActivity(intent);
        }
    }

    @Override
    public void onVideoFullScreen(boolean isFullScreen) {
        if (!isFullScreen) {
            setSwipeBackEnable(PrefKit.getBoolean(this, R.string.pref_swipeback_key, true));
        } else {
            setSwipeBackEnable(false);
        }
    }

    @Override
    public void onShowHtmlVideoView(View html5VideoView) {
        contentView.addView(html5VideoView);
        html5VideoView.bringToFront();
        if(isNewDesignMode){
            pager.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onHideHtmlVideoView(View html5VideoView) {
        contentView.removeView(html5VideoView);
        if(isNewDesignMode){
            pager.setVisibility(View.VISIBLE);
        }
    }


    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
