package com.ywwxhz.service;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.adapter.TopicCommentAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.ResponseObject;
import com.ywwxhz.entity.TopicComment;
import com.ywwxhz.lib.PagedLoader;
import com.ywwxhz.lib.handler.ActionService;
import com.ywwxhz.lib.handler.ExternedGsonHttpResposerHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;

import org.apache.http.Header;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class TopicCommentService extends ActionService implements OnRefreshListener {
    private int current;
    private NetKit mNetKit;
    private Activity mContext;
    private ListView mListView;
    private PagedLoader mLoader;
    private ProgressBar mProgressBar;
    private TopicCommentAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;
    private PagedLoader.OnLoadListener loadListener = new PagedLoader.OnLoadListener() {
        @Override
        public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad) {
            pagedLoader.setFinally();
            //makeRequest();
        }
    };
    private ResponseHandlerInterface handlerInterface;

    public TopicCommentService(final Activity mContext) {
        this.mContext = mContext;
        this.mNetKit = new NetKit(mContext);
        this.mPullToRefreshLayout = new PullToRefreshLayout(mContext);
        this.mProgressBar = (ProgressBar) mContext.findViewById(R.id.loading);
        this.mListView = (ListView) mContext.findViewById(android.R.id.list);
        this.mAdapter = new TopicCommentAdapter(mContext, new ArrayList<TopicComment>());
        this.mLoader = PagedLoader.Builder.getInstance(mContext).setListView(mListView).setOnLoadListener(loadListener).builder();
        this.mLoader.setAdapter(mAdapter);
        ActionBarPullToRefresh.from(mContext)
                .insertLayoutInto((ViewGroup) mContext.findViewById(android.R.id.content))
                .theseChildrenArePullable(mListView)
                .listener(this)
                .options(Options.create().scrollDistance(0.2f).build())
                .setup(mPullToRefreshLayout);
        this.handlerInterface = new TopicNewsHandler(this,new TypeToken<ResponseObject<ArrayList<TopicComment>>>(){});
        loadDataOnStartUp();
    }

    private void loadDataOnStartUp() {
        ArrayList<TopicComment> comments = FileCacheKit.getInstance().getAsObject("newsList".hashCode() + "", "list", new TypeToken<ArrayList<TopicComment>>() {
        });
        mProgressBar.setVisibility(View.VISIBLE);
            this.current = 0;
        if (comments != null) {
            mAdapter.setDataSet(comments);
            mLoader.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }
        makeRequest();
    }

    public void onResume() {
        if (PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_page_key), true)) {
            this.mLoader.setMode(PagedLoader.Mode.AUTO_LOAD);
        } else {
            this.mLoader.setMode(PagedLoader.Mode.CLICK_TO_LOAD);
        }
    }

    public ListView getParentView() {
        return mListView;
    }

    public Activity getContext() {
        return mContext;
    }

    public PullToRefreshLayout getPullToRefreshLayout() {
        return mPullToRefreshLayout;
    }

    @Override
    public void onRefreshStarted(View view) {
        makeRequest();
    }

    private void makeRequest() {
        mNetKit.getTopicComment((current + 1)+"", handlerInterface);
    }

    public void callLoadSuccess(ArrayList<TopicComment> topicComments, boolean from) {
        if (from) {
            mAdapter.setDataSet(topicComments);
        } else {
            mAdapter.getDataSet().addAll(mAdapter.getCount() - 1, topicComments);
        }
    }

//    private void showToastAndCache(int size) {
//        Crouton.makeText(mContext, mContext.getString(R.string.message_new_news, size), Style.INFO).show();
//        FileCacheKit.getInstance().putAsync("newsList".hashCode() + "", Toolkit.getGson().toJson(mAdapter.getDataSet().subList(0, 40)), "list", null);
//    }

    public void setLoadFinish() {
        mLoader.setLoading(false);
        mLoader.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        mPullToRefreshLayout.setRefreshComplete();
    }

    private class TopicNewsHandler extends ExternedGsonHttpResposerHandler<TopicCommentService, ResponseObject<ArrayList<TopicComment>>> {

        private boolean from;

        protected TopicNewsHandler(TopicCommentService mActionServer, TypeToken<ResponseObject<ArrayList<TopicComment>>> type) {
            super(mActionServer, type);
        }

        public boolean isFrom() {
            return from;
        }

        public void setFrom(boolean from) {
            this.from = from;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString, ResponseObject<ArrayList<TopicComment>> object) {
            if("success".equals(object.getState())){
                mActionServer.callLoadSuccess(object.getResult(),from);
            }else{
                onFailure(statusCode,headers,responseString,new Exception("can't load jinhua comment"));
            }
        }
    }
}
