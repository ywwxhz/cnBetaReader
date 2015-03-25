package com.ywwxhz.processers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.activitys.SettingsActivity;
import com.ywwxhz.adapters.BaseAdapter;
import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.entitys.NewsListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.handler.BaseHttpResponseHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.widget.PagedLoader;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:46.
 */
public class NewsListProcesserImpl extends BaseProcesserImpl implements SwipeRefreshLayout.OnRefreshListener {
    private int topSid;
    private int current;
    private boolean hasCached;
    private Activity mContext;
    private ListView mListView;
    private PagedLoader mLoader;
    private BaseAdapter mAdapter;
    private FloatingActionButton actionButton;
    private SwipeRefreshLayout mSwipeLayout;
    private ResponseHandlerInterface newsPage = new BaseHttpResponseHandler<NewsListObject>(new TypeToken<ResponseObject<NewsListObject>>() {
    }){

        @Override
        protected void onSuccess(NewsListObject result) {
            List<NewsItem> itemList = result.getList();
            List<NewsItem> dataSet = mAdapter.getDataSet();
            int size = 0;
            boolean find = false;
            for (int i = 0; i < itemList.size(); i++) {
                NewsItem item = itemList.get(i);
                if (itemList.get(i).getCounter() != null && item.getComments() != null) {
                    int num = Integer.parseInt(item.getCounter());
                    if (num > 9999) {
                        item.setCounter("9999+");
                    }
                    num = Integer.parseInt(item.getComments());
                    if (num > 999) {
                        item.setComments("999+");
                    }
                } else {
                    item.setCounter("0");
                    item.setComments("0");
                }
                StringBuilder sb = new StringBuilder(Html.fromHtml(item.getHometext().replaceAll("<.*?>|[\\r|\\n]", "")));
                if (sb.length() > 140) {
                    item.setSummary(sb.replace(140, sb.length(), "...").toString());
                } else {
                    item.setSummary(sb.toString());
                }
                if (item.getThumb().contains("thumb")) {
                    item.setLargeImage(item.getThumb().replaceAll("(\\.\\w{3,4})?_100x100|thumb/mini/", ""));
                }
                if (!find && item.getSid() != topSid) {
                    size++;
                } else if (!find) {
                    find = true;
                }
            }
            if (!find) {
                size++;
            }

            if (!hasCached || result.getPage() == 1) {
                hasCached = true;
                mAdapter.setDataSet(itemList);
                topSid = itemList.get(1).getSid();
                showToastAndCache(itemList, size - 1);
            } else {
                dataSet.addAll(itemList);
            }
            current = result.getPage();
        }

        @Override
        protected Activity getActivity() {
            return mContext;
        }

        @Override
        public void onFinish() {
            setLoadFinish();
        }
    };

    public NewsListProcesserImpl(final Activity mContext, TranslucentStatusHelper helper) {
        this.hasCached = false;
        this.mContext = mContext;
        this.mListView = (ListView) mContext.findViewById(android.R.id.list);
        mSwipeLayout = (SwipeRefreshLayout) mContext.findViewById(R.id.swipe_container);
        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.statusColor, R.color.toolbarColor, R.color.title_color);
        helper.getOption().setConfigView(mSwipeLayout);
        this.actionButton = (FloatingActionButton) mContext.findViewById(R.id.action);
        TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.type_head, mListView, false);
        view.setText("类型：全部资讯");
        this.mListView.addHeaderView(view, null, false);
        PagedLoader.OnLoadListener loadListener = new PagedLoader.OnLoadListener() {
            @Override
            public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad) {
                NetKit.getInstance().getNewslistByPage(current + 1, "all", newsPage);
            }
        };
        this.mLoader = PagedLoader.Builder.getInstance(mContext).setListView(mListView).setOnLoadListener(loadListener).builder();
        this.mLoader.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        this.actionButton.setVisibility(View.VISIBLE);
        this.actionButton.setImageResource(R.mipmap.ic_settings);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SettingsActivity.class);
                mContext.startActivityForResult(intent, 100);
            }
        });
        this.actionButton.setScaleX(0);
        this.actionButton.setScaleY(0);
        this.actionButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                actionButton.setVisibility(View.VISIBLE);
                actionButton.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(
                        new AccelerateDecelerateInterpolator()).start();
            }
        }, 200);
        this.mAdapter = new NewsListAdapter(mContext, new ArrayList<NewsItem>());
        this.mLoader.setAdapter(mAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NewsListProcesserImpl.this.mContext, NewsDetailActivity.class);
                intent.putExtra(NewsDetailProcesserImpl.NEWS_ITEM_KEY, ((NewsListAdapter)mAdapter).getDataSetItem(i - 1));
                NewsListProcesserImpl.this.mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void loadData(boolean startup) {
        ArrayList<NewsItem> newsList = FileCacheKit.getInstance().getAsObject("newsList".hashCode() + "", "list", new TypeToken<ArrayList<NewsItem>>() {
        });
        if (newsList != null) {
            hasCached = true;
            topSid = newsList.get(1).getSid();
            mAdapter.setDataSet(newsList);
            mLoader.notifyDataSetChanged();
        } else {
            this.hasCached = false;
        }
        this.current = 1;
        if (!hasCached || PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_reflush_key), false)) {
            mListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeLayout.setRefreshing(true);
                    onRefresh();
                }
            }, startup ? 400 : 0);
        }
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

    private void showToastAndCache(List<NewsItem> itemList, int size) {
        if (size < 1) {
            Toolkit.showCrouton(mContext, mContext.getString(R.string.message_no_new_news), Style.CONFIRM);
        } else {
            Toolkit.showCrouton(mContext, mContext.getString(R.string.message_new_news, size), Style.INFO);
        }
        FileCacheKit.getInstance().putAsync("newsList".hashCode() + "", Toolkit.getGson().toJson(itemList), "list", null);
    }

    public void setLoadFinish() {
        if (mLoader.getLoading()) {
            mLoader.setLoading(false);
        }
        mLoader.notifyDataSetChanged();
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
    }

    public void onReturn(int request, int response) {
        if (request == 100 && response == 200) {
            mAdapter.notifyDataSetChanged(true);
        }
    }

    @Override
    public void onRefresh() {
        NetKit.getInstance().getNewslistByPage(1, "all", newsPage);
    }
}
