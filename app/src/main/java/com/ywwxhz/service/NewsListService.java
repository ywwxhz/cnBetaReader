package com.ywwxhz.service;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.melnykov.fab.FloatingActionButton;
import com.ywwxhz.adapter.NewsListAdapter;
import com.ywwxhz.app.NewsDetailActivity;
import com.ywwxhz.app.SettingsActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.NewsItem;
import com.ywwxhz.entity.NewsListObject;
import com.ywwxhz.entity.ResponseObject;
import com.ywwxhz.lib.PagedLoader;
import com.ywwxhz.lib.handler.ActionService;
import com.ywwxhz.lib.handler.NormalNewsListHandler;
import com.ywwxhz.lib.handler.RealTimeNewListHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NewsListService extends ActionService implements OnRefreshListener {
    private int current;
    private NetKit mNetKit;
    private boolean hasCached;
    private Activity mContext;
    private ListView mListView;
    private PagedLoader mLoader;
    private ProgressBar mProgressBar;
    private NewsListAdapter mAdapter;
    private FloatingActionButton actionButton;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ResponseHandlerInterface realtimeNews = new RealTimeNewListHandler(this, new TypeToken<ResponseObject<ArrayList<NewsItem>>>() {
    });
    private ResponseHandlerInterface newsPage = new NormalNewsListHandler(this, new TypeToken<ResponseObject<NewsListObject>>() {
    });
    private PagedLoader.OnLoadListener loadListener = new PagedLoader.OnLoadListener() {
        @Override
        public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad) {
            String sid = "";
            if (mAdapter.getCount() > 0) {
                sid = mAdapter.getDataSetItem(mAdapter.getCount() - 1).getSid() + "";
            }
            mNetKit.getNewslistByPage(sid, current + 1, newsPage);
        }
    };

    public NewsListService(final Activity mContext) {
        this.hasCached = false;
        this.mContext = mContext;
        this.mNetKit = new NetKit(mContext);
        this.mPullToRefreshLayout = new PullToRefreshLayout(mContext);
        this.mProgressBar = (ProgressBar) mContext.findViewById(R.id.loading);
        this.mListView = (ListView) mContext.findViewById(android.R.id.list);
        this.actionButton = (FloatingActionButton) mContext.findViewById(R.id.action);
        this.mAdapter = new NewsListAdapter(mContext, new ArrayList<NewsItem>());
        this.mLoader = PagedLoader.Builder.getInstance(mContext).setListView(mListView).setOnLoadListener(loadListener).builder();
        this.mLoader.setAdapter(mAdapter);
        this.mLoader.setOnScrollListener(this.actionButton.getListViewOnScrollListener());
        this.actionButton.setVisibility(View.VISIBLE);
        this.actionButton.attachToListView(mListView, false);
        this.actionButton.setImageResource(R.drawable.ic_settings);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                mContext.startActivity(intent);
            }
        });
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NewsListService.this.mContext, NewsDetailActivity.class);
                intent.putExtra(NewsDetailService.NEWS_ITEM_KEY, mAdapter.getDataSetItem(i));
                NewsListService.this.mContext.startActivity(intent);
            }
        });
        ActionBarPullToRefresh.from(mContext)
                .insertLayoutInto((ViewGroup) mContext.findViewById(android.R.id.content))
                .theseChildrenArePullable(mListView)
                .listener(this)
                .options(Options.create().scrollDistance(0.2f).build())
                .setup(mPullToRefreshLayout);
        loadDataOnStartUp();
    }

    private void loadDataOnStartUp() {
        ArrayList<NewsItem> newsList = FileCacheKit.getInstance().getAsObject("newsList".hashCode() + "", "list", new TypeToken<ArrayList<NewsItem>>() {
        });
        mProgressBar.setVisibility(View.VISIBLE);
        if (newsList != null) {
            mAdapter.setDataSet(newsList);
            mLoader.notifyDataSetChanged();
            this.hasCached = true;
            this.current = 1;
            mProgressBar.setVisibility(View.GONE);
        } else {
            this.hasCached = false;
            this.current = 0;
        }
    }

    public void onResume() {
        if (!hasCached || PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_reflush_key), true)) {
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshLayout.setRefreshing(true);
                    onRefreshStarted(null);
                }
            }, 300);
        }
        if (PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_page_key), true)) {
            this.mLoader.setMode(PagedLoader.Mode.AUTO_LOAD);
        } else {
            this.mLoader.setMode(PagedLoader.Mode.CLICK_TO_LOAD);
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public Activity getContext() {
        return mContext;
    }

    public NewsListAdapter getAdapter() {
        return mAdapter;
    }

    public PagedLoader getLoader() {
        return mLoader;
    }

    public PullToRefreshLayout getPullToRefreshLayout() {
        return mPullToRefreshLayout;
    }

    @Override
    public void onRefreshStarted(View view) {
        ResponseHandlerInterface handlerInterface;
        String sid = "";
        if (mAdapter.getCount() == 0) {
            handlerInterface = newsPage;
        } else {
            handlerInterface = realtimeNews;
            sid = mAdapter.getDataSetItem(0).getSid() + "";
        }
        mNetKit.getRealtimeNews(sid, handlerInterface);
    }

    public void callRealTimeNewsLoadSuccess(List<NewsItem> itemListes) {
        List<NewsItem> ds = mAdapter.getDataSet();
        for (NewsItem item : itemListes) {
            item.setHometext(item.getHometext().replaceAll("<.*?>", ""));
            ds.add(0, item);
        }
        showToastAndCache(itemListes.size());
    }

    public void callNewsPageLoadSuccess(NewsListObject listPage) {
        List<NewsItem> itemList = listPage.getList();
        List<NewsItem> dataSet = mAdapter.getDataSet();
        for (NewsItem item : itemList) {
            item.setHometext(item.getHometext().replaceAll("<.*?>", ""));
        }

        if (dataSet.size() >= 40) {
            int firstSid = dataSet.get(dataSet.size() - 40).getSid();
            int lastSid = dataSet.get(dataSet.size() - 1).getSid();
            if (itemList.get(0).getSid() > firstSid) {
                dataSet.addAll(itemList);
            } else {
                for (NewsItem item : itemList) {
                    int currentSid = item.getSid();
                    if (currentSid >= lastSid) {
                        continue;
                    }
                    dataSet.add(item);
                }
            }
        } else {
            dataSet.addAll(itemList);
        }
        current = listPage.getPage();
        if (!hasCached) {
            hasCached = true;
            showToastAndCache(itemList.size());
        }
    }

    private void showToastAndCache(int size) {
        Crouton.makeText(mContext, mContext.getString(R.string.message_new_news, size), Style.INFO).show();
        FileCacheKit.getInstance().putAsync("newsList".hashCode() + "", Toolkit.getGson().toJson(mAdapter.getDataSet().subList(0, 40)), "list", null);
    }

    public void setLoadFinish() {
        mLoader.setLoading(false);
        mLoader.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        mPullToRefreshLayout.setRefreshComplete();
    }

    public View getFloatButtom() {
        return actionButton;
    }
}
