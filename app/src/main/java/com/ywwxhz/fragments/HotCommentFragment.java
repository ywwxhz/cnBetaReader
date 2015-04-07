package com.ywwxhz.fragments;

import com.ywwxhz.adapters.HotCommentAdapter;
import com.ywwxhz.data.impl.NetHotCommentDataProvider;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.processers.BaseListProcesser;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:00.
 */
public class HotCommentFragment extends BaseListFragment<HotCommentItem,HotCommentAdapter,NetHotCommentDataProvider,BaseListProcesser<HotCommentItem,NetHotCommentDataProvider>> {

    @Override
    public NetHotCommentDataProvider getProvider() {
        return new NetHotCommentDataProvider(getActivity());
    }

    @Override
    protected BaseListProcesser<HotCommentItem, NetHotCommentDataProvider> createProcesser(NetHotCommentDataProvider provider) {
        return new BaseListProcesser<>(provider);
    }
}
