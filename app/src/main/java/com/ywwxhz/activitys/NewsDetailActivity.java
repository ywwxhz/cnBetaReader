package com.ywwxhz.activitys;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.BaseFragment;
import com.ywwxhz.fragments.NewsCommentFragment;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.processers.BaseProcesserImpl;
import com.ywwxhz.widget.FixViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻详情界面
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:52.
 */
public class NewsDetailActivity extends ExtendBaseActivity implements NewsDetailFragment.NewsDetailCallBack {
    private static final String TAG = "NewsDetailActivity";
    private FixViewPager pager;
    private FragmentAdapter adapter;
    private CharSequence title;
    private int orientation;
    private int requiredOrientation;
    private int oldSystemUIVisuablity;

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "onTrimMemory: " + toString() + " " + level);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.finish();
            return;
        }
        Log.i(TAG, "onCreate: " + toString() + " " + getIntent().getExtras());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(NewsDetailFragment.NEWS_URL_KEY)
                && bundle.containsKey(NewsDetailFragment.NEWS_TITLE_KEY)) {
            oldSystemUIVisuablity = getRootView().getSystemUiVisibility();
            title = bundle.getString(NewsDetailFragment.NEWS_TITLE_KEY);
            if (!TextUtils.isEmpty(title)) {
                setTitle(title);
            }
            setContentView(R.layout.pager_layout);
            pager = (FixViewPager) findViewById(R.id.pager);
            adapter = new FragmentAdapter(getSupportFragmentManager());
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // do nothing
                }

                @Override
                public void onPageSelected(int position) {
                    updateTitle();
                    if (position == 0) {
                        setSwipeBackEnable(
                                PrefKit.getBoolean(NewsDetailActivity.this, R.string.pref_swipeback_key, true));
                    } else {
                        setSwipeBackEnable(false);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // do nothing
                }
            });
            pager.setAdapter(adapter);
            adapter.add(NewsDetailFragment.getInstance(bundle.getString(NewsDetailFragment.NEWS_URL_KEY),
                    title.toString()));
        } else {
            Toast.makeText(this, "缺少必要参数", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
            return true;
        }
        return adapter.getFragment(pager.getCurrentItem()).onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public void onNewsLoadFinish(NewsItem item, boolean success) {
        if (success && adapter != null && adapter.getCount() == 1) {
            adapter.add(NewsCommentFragment.getInstance(item.getSid(), item.getSN())
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
    public void commentAction(int sid, String sn, String title) {
        pager.setCurrentItem(1, true);
    }

    @Override
    public void onVideoFullScreen(boolean isFullScreen) {
        if (!isFullScreen) {
            setRequestedOrientation(orientation);
            setRequestedOrientation(requiredOrientation);
            getSupportActionBar().show();
            setSwipeBackEnable(PrefKit.getBoolean(this, R.string.pref_swipeback_key, true));
            helper.setEnable(true);
            if (Build.VERSION_CODES.JELLY_BEAN < Build.VERSION.SDK_INT) {
                getRootView().setSystemUiVisibility(oldSystemUIVisuablity);
            }
        } else {
            requiredOrientation = getRequestedOrientation();
            orientation = getResources().getConfiguration().orientation;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getSupportActionBar().hide();
            setSwipeBackEnable(false);
            helper.setEnable(false);
            if (Build.VERSION_CODES.JELLY_BEAN < Build.VERSION.SDK_INT) {
                getRootView().setSystemUiVisibility(
                        oldSystemUIVisuablity | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN // hide
                                                                                                                       // status
                                                                                                                       // bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
    }

    @Override
    public void onShowHtmlVideoView(View html5VideoView) {
        getRootView().addView(html5VideoView);
        html5VideoView.bringToFront();
        pager.setVisibility(View.GONE);
    }

    @Override
    public void onHideHtmlVideoView(View html5VideoView) {
        getRootView().removeView(html5VideoView);
        pager.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (pager != null) {
            switch (pager.getCurrentItem()) {
            case 0:
                super.setTitle("详情：" + title);
                break;
            case 1:
                super.setTitle("评论：" + title);
                break;
            }
        } else {
            super.setTitle("详情：" + title);
        }
        this.title = title;
    }

    /**
     * 更新 Activity 标题
     */
    private void updateTitle() {
        setTitle(title);
    }

    @Override
    public String toString() {
        return "NewsDetailActivity{" + "hashCode=" + hashCode() + ", pager=" + pager + ", adapter=" + adapter
                + ", title=" + title + ", orientation=" + orientation + ", requiredOrientation=" + requiredOrientation
                + ", oldSystemUIVisuablity=" + oldSystemUIVisuablity + '}';
    }

    class FragmentAdapter extends FragmentPagerAdapter {
        private List<BaseFragment> fragments = new ArrayList<>(2);

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

        public List<BaseFragment> getFragments() {
            return fragments;
        }

        public FragmentAdapter setFragments(List<BaseFragment> fragments) {
            this.fragments = fragments;
            notifyDataSetChanged();
            return this;
        }

        public BaseFragment getFragment(int pos) {
            return fragments.get(pos);
        }

        public FragmentAdapter add(BaseFragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
            return this;
        }
    }
}
