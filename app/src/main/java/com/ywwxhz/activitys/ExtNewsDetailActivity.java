package com.ywwxhz.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.processers.BaseProcesserImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class ExtNewsDetailActivity extends ExtendBaseActivity implements NewsDetailFragment.NewsDetailCallBack {

    private List<Fragment> fragments = new ArrayList<>(2);
    private ViewPager pager;
    private FragmentAdapter adapter;
    private boolean showVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getIntent().getExtras().containsKey(NewsDetailFragment.NEWS_ITEM_KEY)) {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.pager_layout);
        final NewsItem item = (NewsItem) getIntent().getSerializableExtra(NewsDetailFragment.NEWS_ITEM_KEY);
        setTitle("详情：" + item.getTitle());
        fragments.add(NewsDetailFragment.getInstance(item));
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setTitle("详情：" + item.getTitle());
                    setSwipeBackEnable(PrefKit.getBoolean(ExtNewsDetailActivity.this, R.string.pref_swipeback_key, true));
                } else {
                    setTitle("评论：" + item.getTitle());
                    setSwipeBackEnable(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public void onNewsLoadFinish(NewsItem item, boolean success) {
        if (success && fragments.size() == 1) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
            return true;
        }
        return ((NewsDetailFragment) fragments.get(0)).onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void CommentAction(int sid, String sn, String title) {
        pager.setCurrentItem(1, true);
    }

    @Override
    public void onShowVideo(boolean showVideo) {
        this.showVideo = showVideo;
        adapter.notifyDataSetChanged();
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
            if (showVideo) {
                return 1;
            } else {
                return fragments.size();
            }
        }
    }
}
