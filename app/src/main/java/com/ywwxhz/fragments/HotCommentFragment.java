package com.ywwxhz.fragments;

import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.data.impl.NetHotCommentDataProvider;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:00.
 */
public class HotCommentFragment extends BaseNewsListFragment {
    @Override
    public ListDataProvider getProvider() {
        return new NetHotCommentDataProvider(getActivity());
    }
}
