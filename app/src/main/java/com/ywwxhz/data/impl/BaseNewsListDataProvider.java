package com.ywwxhz.data.impl;

import android.app.Activity;

import com.ywwxhz.adapters.BaseAdapter;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.NewsItem;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/4 22:17.
 */
public abstract class BaseNewsListDataProvider<Adapter extends BaseAdapter<NewsItem>> extends ListDataProvider<NewsItem,Adapter> {
    public BaseNewsListDataProvider(Activity activity) {
        super(activity);
    }
}
