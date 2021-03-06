package com.ywwxhz.fragments;

import android.os.Bundle;
import android.view.View;

import com.ywwxhz.adapters.CommentListAdapter;
import com.ywwxhz.data.impl.NewsCommentProvider;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.processers.NewsCommentProcesser;

/**
 * 新闻评论 Fragment
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/27 15:31.
 */
public class NewsCommentFragment extends BaseListFragment<CommentItem,CommentListAdapter,NewsCommentProvider,NewsCommentProcesser> {
    public static final String SN_KEY = "key_sn";
    public static final String SID_KEY = "key_sid";
    private int sid;
    private String sn;
    private boolean hasinit;

    public static NewsCommentFragment getInstance(int sid,String sn){
        Bundle args = new Bundle();
        args.putInt(SID_KEY,sid);
        args.putString(SN_KEY,sn);
        NewsCommentFragment f = new NewsCommentFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null&&args.containsKey(SID_KEY)&&args.containsKey(SN_KEY)){
            sid = args.getInt(SID_KEY);
            sn = args.getString(SN_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    protected NewsCommentProvider getProvider() {
        return new NewsCommentProvider(getActivity());
    }

    @Override
    protected NewsCommentProcesser createProcesser(NewsCommentProvider provider) {
        return new NewsCommentProcesser(provider);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        processer.setParams(sid,sn);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean hasMenu() {
        return true;
    }

}
