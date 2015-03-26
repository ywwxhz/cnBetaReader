package com.ywwxhz.processers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.DataProviderCallback;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.widget.PagedLoader;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:46.
 */
public class NewsListProcesserImpl extends BaseProcesserImpl implements SwipeRefreshLayout.OnRefreshListener, DataProviderCallback {

    private Activity mContext;
    private PagedLoader mLoader;
    private SwipeRefreshLayout mSwipeLayout;
    private ListDataProvider provider;

    public NewsListProcesserImpl(FragmentActivity activity, View view, ListDataProvider provider) {
        this.mContext = activity;
        this.provider = provider;
        provider.setCallback(this);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.statusColor, R.color.toolbarColor, R.color.title_color);
        TextView headView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.type_head, listView, false);
        headView.setText("类型：" + provider.getTypeName());
        listView.addHeaderView(headView, null, false);
        PagedLoader.OnLoadListener loadListener = new PagedLoader.OnLoadListener() {
            @Override
            public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad) {
                NewsListProcesserImpl.this.provider.loadNextData();
            }
        };
        this.mLoader = PagedLoader.from(listView).setOnLoadListener(loadListener).builder();
        this.mLoader.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        this.mLoader.setAdapter(this.provider.getAdapter());
        listView.setOnItemClickListener(provider.getOnItemClickListener());
    }

    @Override
    public void loadData(final boolean startup) {
        Toolkit.runInUIThread(new Runnable() {
            @Override
            public void run() {
                provider.loadData(startup);
                if (!provider.isCached() || PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_reflush_key), false)) {
                    mSwipeLayout.setRefreshing(true);
                    onRefresh();
                }
            }
        }, startup ? 200 : 0);
    }

    public void onResume() {
        if (PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_page_key), false)) {
            this.mLoader.setMode(PagedLoader.Mode.AUTO_LOAD);
        } else {
            this.mLoader.setMode(PagedLoader.Mode.CLICK_TO_LOAD);
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void onRefresh() {
        provider.loadNewData();
    }

    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadFinish() {
        provider.getAdapter().notifyDataSetChanged();
        if (provider.getAdapter().getCount() < provider.getPageSize()) {
            mLoader.setFinally();
        } else {
            mLoader.setLoading(false);
        }
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
    }
}
