package com.ywwxhz.fragments;

import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.data.impl.FavoriteNewsListDataProvider;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:27.
 */
public class FavoriteNewsListFragment extends BaseNewsListFragment {
    @Override
    public ListDataProvider getProvider() {
        return new FavoriteNewsListDataProvider(getActivity());
    }
}
