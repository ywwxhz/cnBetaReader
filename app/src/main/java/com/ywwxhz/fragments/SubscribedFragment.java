package com.ywwxhz.fragments;

import com.ywwxhz.adapters.TopicListAdapter;
import com.ywwxhz.data.impl.TopicScribedDataProvider;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.processers.SubscribedProcesser;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 17:08.
 */
public class SubscribedFragment extends BaseListFragment<TopicItem,TopicListAdapter,TopicScribedDataProvider,SubscribedProcesser> {

    @Override
    public TopicScribedDataProvider getProvider() {
        return new TopicScribedDataProvider(getActivity());
    }

    @Override
    protected SubscribedProcesser createProcesser(TopicScribedDataProvider provider) {
        return new SubscribedProcesser(provider);
    }


}
