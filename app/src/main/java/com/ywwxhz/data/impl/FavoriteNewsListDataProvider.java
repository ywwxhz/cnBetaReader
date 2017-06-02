package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.FavoriteListAdapter;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.database.sqlite.Selector;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:28.
 */
public class FavoriteNewsListDataProvider extends BaseNewsListDataProvider<FavoriteListAdapter> {
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
        List<NewsItem> items;
        int old = current;
        try {
            current = 0;
            items = MyApplication.getInstance().getDbUtils()
                    .findAll(Selector.from(NewsItem.class).limit(getPageSize()).offset(0).orderBy("sid", true));
            if (items == null) {
                items = new ArrayList<>(0);
            }
            getAdapter().setDataSet(items);
        } catch (DbException e) {
            current = old;
            items = new ArrayList<>();
        }
        if (callback != null) {
            callback.onLoadFinish(items.size());
        }
    }

    @Override
    public void loadNextData() {
        List<NewsItem> items;
        try {
            current++;
            items = MyApplication.getInstance().getDbUtils().<NewsItem>findAll(Selector.from(NewsItem.class)
                    .limit(getPageSize()).offset(current * getPageSize()).orderBy("sid", true));
            getAdapter().getDataSet().addAll(items);
        } catch (DbException e) {
            current--;
            items = new ArrayList<>();
            e.printStackTrace();
        }
        if (callback != null) {
            callback.onLoadFinish(items.size());
        }
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                NewsItem item = getAdapter().getDataSetItem(i - 1);
                if (item.getUrl_show() == null) {
                    intent.putExtra(NewsDetailFragment.NEWS_URL_KEY,
                            Configure.buildArticleUrl(String.valueOf(item.getSid())));
                } else {
                    intent.putExtra(NewsDetailFragment.NEWS_URL_KEY, item.getUrl_show());
                }
                intent.putExtra(NewsDetailFragment.NEWS_TITLE_KEY, item.getTitle());
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {

    }
}
