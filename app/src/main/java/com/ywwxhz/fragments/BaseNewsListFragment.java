package com.ywwxhz.fragments;

import com.ywwxhz.adapters.BaseAdapter;
import com.ywwxhz.data.impl.BaseNewsListDataProvider;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.processers.NewsListProcesser;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/4 17:43.
 */
public abstract class BaseNewsListFragment<Adapter extends BaseAdapter<NewsItem>,Provider extends BaseNewsListDataProvider<Adapter>>
        extends BaseListFragment<NewsItem,Adapter,Provider,NewsListProcesser<Provider>> {
    @Override
    protected NewsListProcesser<Provider> createProcesser(Provider provider) {
        return new NewsListProcesser<>(provider);
    }
}
