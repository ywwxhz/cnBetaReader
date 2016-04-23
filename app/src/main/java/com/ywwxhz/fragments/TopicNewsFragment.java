package com.ywwxhz.fragments;

import android.os.Bundle;

import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.data.impl.NetNewsListDataProvider;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.lib.handler.BaseCallback;
import com.ywwxhz.lib.kits.NetKit;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2016/2/14 22:18.
 */
public class TopicNewsFragment extends BaseNewsListFragment {

    public static final String TOPIC_KEY = "key_topic";

    private TopicItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments!=null&&arguments.containsKey(TOPIC_KEY)) {
            item = (TopicItem) arguments.getSerializable(TOPIC_KEY);
        }
    }

    public static TopicNewsFragment getInstance(TopicItem item) {
        TopicNewsFragment fragment = new TopicNewsFragment();
        Bundle args = new Bundle(1);
        args.putSerializable(TOPIC_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ListDataProvider getProvider() {
        return new NetNewsListDataProvider(getActivity()) {
            @Override
            public String getTypeKey() {
                return item.getTopicId();
            }

            @Override
            public String getTypeName() {
                return item.getTopicName();
            }

            @Override
            public void makeRequest(int page, String type, BaseCallback handlerInterface) {
                NetKit.getNewslistByTopic(getActivity(),page, type, handlerInterface);
            }

            @Override
            public int getPageSize() {
                return 40;
            }
        };
    }

    @Override
    public boolean hasMenu() {
        return true;
    }
}
