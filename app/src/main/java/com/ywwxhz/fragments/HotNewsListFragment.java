package com.ywwxhz.fragments;

import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.data.impl.NetNewsListDataProvider;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 22:55.
 */
public class HotNewsListFragment extends BaseNewsListFragment<NewsListAdapter,NetNewsListDataProvider> {
    @Override
    public NetNewsListDataProvider getProvider() {
        return new NetNewsListDataProvider(getActivity()){
            @Override
            public String getTypeKey() {
                return "dig";
            }

            @Override
            public String getTypeName() {
                return "人气推荐";
            }
        };
    }
}
