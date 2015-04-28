package com.ywwxhz.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/27 15:30.
 */
public class NewsDetailFragment extends Fragment {
    public static final String NEWS_ITEM_KEY = "key_news_item";
    private NewsDetailProcesser processer;
    private NewsItem item;

    public interface NewsDetailCallBack{
        void onNewsLoadFinish(NewsItem item,boolean success);
        void CommentAction(int sid,String sn,String title);
        void onShowVideo(boolean showVideo);
    }

    public static NewsDetailFragment getInstance(NewsItem item){
        Bundle args = new Bundle();
        args.putSerializable(NEWS_ITEM_KEY, item);
        NewsDetailFragment f = new NewsDetailFragment();
        f.setArguments(args);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if(args!=null&&args.containsKey(NEWS_ITEM_KEY)){
            item = (NewsItem) args.getSerializable(NEWS_ITEM_KEY);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(processer==null) {
            processer = new NewsDetailProcesser(new NewsDetailProvider(activity));
        }
        processer.setActivity((ActionBarActivity) activity);
        if(activity instanceof NewsDetailCallBack){
            processer.setCallBack((NewsDetailCallBack) activity);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail,container,false);
        processer.assumeView(view);
        processer.setNewsItem(item);
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
        processer.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return processer.onOptionsItemSelected(item)||super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        processer.onResume();
    }

    @Override
    public void onPause() {
        super.onStop();
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

    public boolean onKeyDown(int keyCode, KeyEvent event){
        return processer.onKeyDown(keyCode, event);
    }
}
