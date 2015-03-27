package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.FavoriteListAdapter;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.database.sqlite.Selector;
import com.ywwxhz.processers.NewsDetailProcesserImpl;

import java.util.ArrayList;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:28.
 */
public class FavoriteNewsListDataProvider extends ListDataProvider<FavoriteListAdapter> {
    private int current;

    public FavoriteNewsListDataProvider(Activity mActivity) {
        super(mActivity);
        hasCached = false;
        setPageSize(20);
    }

    @Override
    protected FavoriteListAdapter newAdapter() {
        return new FavoriteListAdapter(getActivity(), new ArrayList<NewsItem>());
    }

    @Override
    public String getTypeKey() {
        return "favorite";
    }

    @Override
    public String getTypeName() {
        return "收藏新闻";
    }

    @Override
    public void loadNewData() {
        int old = current;
        try {
            current = 0;
            getAdapter().setDataSet(MyApplication.getInstance().getDbUtils().<NewsItem>findAll(
                            Selector.from(NewsItem.class).limit(getPageSize()).offset(current * getPageSize()).orderBy("sid",true))
            );
        } catch (DbException e) {
            current = old;
            e.printStackTrace();
        }
        if(callback!=null){
            callback.onLoadFinish();
        }
    }

    @Override
    public void loadNextData() {
        try {
            current++;
            getAdapter().getDataSet()
                    .addAll(MyApplication.getInstance().getDbUtils().<NewsItem>findAll(
                                    Selector.from(NewsItem.class).limit(getPageSize()).offset(current * getPageSize()).orderBy("sid",true))
                    );
        } catch (DbException e) {
            current--;
            e.printStackTrace();
        }
        if(callback!=null){
            callback.onLoadFinish();
        }
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailProcesserImpl.NEWS_ITEM_KEY, getAdapter().getDataSetItem(i - 1));
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {

    }
}
