package com.ywwxhz.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.NewsDetailProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.processers.NewsDetailProcesser;

/**
 * 新闻详情 Fragment
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/27 15:30.
 */
public class NewsDetailFragment extends BaseFragment {
    public static final String NEWS_SID_KEY = "key_news_sid";
    public static final String NEWS_TITLE_KEY = "key_news_title";
    private static final String TAG = "NewsDetailFragment";
    private NewsDetailProcesser processer;
    private int sid;
    private String title;

    /**
     * 创建实例
     * 
     * @param sid
     *            新闻编号
     * @param title
     *            新闻标题
     * @return
     */
    public static NewsDetailFragment getInstance(int sid, String title) {
        NewsDetailFragment f = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putInt(NEWS_SID_KEY, sid);
        args.putString(NEWS_TITLE_KEY, title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null && args.containsKey(NEWS_SID_KEY) && args.containsKey(NEWS_TITLE_KEY)) {
            sid = args.getInt(NEWS_SID_KEY);
            title = args.getString(NEWS_TITLE_KEY);
        }
        Log.i(TAG, "onCreate: " + toString() + " " + args);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (processer == null) {
            processer = new NewsDetailProcesser(new NewsDetailProvider(activity));
        }
        processer.setActivity((AppCompatActivity) activity);
        if (activity instanceof NewsDetailCallBack) {
            processer.setCallBack((NewsDetailCallBack) activity);
        }
        Log.i(TAG, "onAttach: " + toString() + " " + activity + " " + processer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: " + toString() + " " + processer);
        View view = inflater.inflate(R.layout.activity_detail, container, false);
        processer.assumeView(view);
        processer.setNewsItem(sid, title);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        processer.loadData(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        processer.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return processer.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        processer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        processer.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        processer.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        processer.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: " + toString() + " " + processer);
        return processer.onKeyDown(keyCode, event);
    }

    @Override
    public String toString() {
        return "NewsDetailFragment{" + "hashCode=" + hashCode() + ", processer=" + processer + ", sid=" + sid
                + ", title='" + title + '\'' + '}';
    }

    /**
     * 新闻详情回调
     */
    public interface NewsDetailCallBack {
        /**
         * 新闻加载完成
         *
         * @param item
         *            新闻
         * @param success
         *            是否成功
         */
        void onNewsLoadFinish(NewsItem item, boolean success);

        /**
         * 点击评论按纽
         * 
         * @param sid
         *            新闻SID
         * @param sn
         *            新闻SN
         * @param title
         *            新闻标题
         */
        void commentAction(int sid, String sn, String title);

        /**
         * 视频全屏回调
         * 
         * @param isFullScreen
         *            是否全屏
         */
        void onVideoFullScreen(boolean isFullScreen);

        /**
         * 进入到全屏事件回调
         * 
         * @param html5VideoView
         */
        void onShowHtmlVideoView(View html5VideoView);

        /**
         * 退出全屏事件回调
         * 
         * @param html5VideoView
         */
        void onHideHtmlVideoView(View html5VideoView);
    }
}
