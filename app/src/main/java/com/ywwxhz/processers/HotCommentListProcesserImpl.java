//package com.ywwxhz.processers;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.animation.AccelerateDecelerateInterpolator;
//import android.widget.AdapterView;
//import android.widget.FrameLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.google.gson.reflect.TypeToken;
//import com.loopj.android.http.ResponseHandlerInterface;
//import com.melnykov.fab.FloatingActionButton;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
//import com.ywwxhz.activitys.NewsDetailActivity;
//import com.ywwxhz.activitys.SettingsActivity;
//import com.ywwxhz.adapters.HotCommentAdapter;
//import com.ywwxhz.cnbetareader.R;
//import com.ywwxhz.entitys.HotCommentItem;
//import com.ywwxhz.entitys.NewsItem;
//import com.ywwxhz.entitys.ResponseObject;
//import com.ywwxhz.lib.Configure;
//import com.ywwxhz.lib.handler.HotCommentListHandler;
//import com.ywwxhz.lib.kits.FileCacheKit;
//import com.ywwxhz.lib.kits.NetKit;
//import com.ywwxhz.lib.kits.PrefKit;
//import com.ywwxhz.lib.kits.Toolkit;
//import com.ywwxhz.widget.PagedLoader;
//import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//
//import de.keyboardsurfer.android.widget.crouton.Crouton;
//import de.keyboardsurfer.android.widget.crouton.Style;
//
///**
// * Created by ywwxhz on 2014/11/1.
// */
//public class HotCommentListProcesserImpl extends BaseProcesserImpl implements SwipeRefreshLayout.OnRefreshListener {
//    private int topSid;
//    private int current;
//    private boolean hasCached;
//    private Activity mContext;
//    private ListView mListView;
//    private PagedLoader mLoader;
//    private HotCommentAdapter mAdapter;
//    private FloatingActionButton actionButton;
//    private SwipeRefreshLayout mSwipeLayout;
//    private TranslucentStatusHelper helper;
//    private ResponseHandlerInterface newsPage = new HotCommentListHandler(this, new TypeToken<ResponseObject<List<HotCommentItem>>>() {
//    });
//    private PagedLoader.OnLoadListener loadListener = new PagedLoader.OnLoadListener() {
//        @Override
//        public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad) {
//            NetKit.getInstance().getNewslistByPage(current + 1, getNewsType(), newsPage);
//        }
//    };
//
//    public String getNewsType(){
//        return "jhcomment";
//    }
//    public String getNewsTypeName(){
//        return "精华评论";
//    }
//
//    public HotCommentListProcesserImpl(final Activity mContext, TranslucentStatusHelper helper) {
//        this.hasCached = false;
//        this.helper = helper;
//        this.mContext = mContext;
//        this.mListView = (ListView) mContext.findViewById(android.R.id.list);
//        mSwipeLayout = (SwipeRefreshLayout) mContext.findViewById(R.id.swipe_container);
//        mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
//        mSwipeLayout.setOnRefreshListener(this);
//        mSwipeLayout.setColorSchemeResources(R.color.statusColor, R.color.toolbarColor, R.color.title_color);
//        this.helper.getOption().setConfigView(mSwipeLayout);
//        this.actionButton = (FloatingActionButton) mContext.findViewById(R.id.action);
//        this.mAdapter = new HotCommentAdapter(mContext,new ArrayList<HotCommentItem>());
//        TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.type_head, mListView, false);
//        view.setText("类型："+getNewsTypeName());
//        this.mListView.addHeaderView(view, null, false);
//        this.mLoader = PagedLoader.Builder.getInstance(mContext).setListView(mListView).setOnLoadListener(loadListener).builder();
//        this.mLoader.setAdapter(mAdapter);
//        this.mLoader.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
//        this.actionButton.setVisibility(View.VISIBLE);
//        this.actionButton.setImageResource(R.mipmap.ic_settings);
//        this.actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, SettingsActivity.class);
//                mContext.startActivityForResult(intent, 100);
//            }
//        });
//        this.actionButton.setScaleX(0);
//        this.actionButton.setScaleY(0);
//        this.actionButton.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                actionButton.setVisibility(View.VISIBLE);
//                actionButton.animate().scaleX(1).scaleY(1).setDuration(500).setInterpolator(
//                        new AccelerateDecelerateInterpolator()).start();
//            }
//        }, 200);
//        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(HotCommentListProcesserImpl.this.mContext, NewsDetailActivity.class);
//                NewsItem item = new NewsItem();
//                HotCommentItem commentItem = mAdapter.getDataSetItem(i - 1);
//                item.setSid(commentItem.getSid());
//                item.setTitle(commentItem.getNewstitle());
//                intent.putExtra(NewsDetailProcesserImpl.NEWS_ITEM_KEY, item);
//                HotCommentListProcesserImpl.this.mContext.startActivity(intent);
//            }
//        });
//
//        loadData(true);
//        fixPos();
//    }
//
//    protected void loadData(boolean startup) {
//        NetKit.getInstance().getNewslistByPage(1, getNewsType(), newsPage);
////        ArrayList<NewsItem> newsList = FileCacheKit.getInstance().getAsObject("newsList".hashCode() + "", "list", new TypeToken<ArrayList<NewsItem>>() {
////        });
////        if (newsList != null) {
////            hasCached = true;
////            topSid = newsList.get(1).getSid();
////            mAdapter.setDataSet(newsList);
////            mLoader.notifyDataSetChanged();
////        } else {
////            this.hasCached = false;
////        }
////        this.current = 1;
////        if (!hasCached || PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_reflush_key), false)) {
////            mListView.postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    mSwipeLayout.setRefreshing(true);
////                    onRefresh();
////                }
////            }, startup ? 400 : 0);
////        }
//    }
//
//    public void onResume() {
//        if (PrefKit.getBoolean(mContext, mContext.getString(R.string.pref_auto_page_key), false)) {
//            this.mLoader.setMode(PagedLoader.Mode.AUTO_LOAD);
//        } else {
//            this.mLoader.setMode(PagedLoader.Mode.CLICK_TO_LOAD);
//        }
//    }
//
//    public Activity getContext() {
//        return mContext;
//    }
//
//    @Override
//    public Activity getActivity() {
//        return mContext;
//    }
//
////    public void callNewsPageLoadSuccess(NewsListObject listPage) {
////        List<NewsItem> itemList = listPage.getList();
////        List<NewsItem> dataSet = mAdapter.getDataSet();
////        int size = 0;
////        boolean find = false;
////        for (int i = 0; i < itemList.size(); i++) {
////            NewsItem item = itemList.get(i);
////            if (itemList.get(i).getCounter() != null && item.getComments() != null) {
////                int num = Integer.parseInt(item.getCounter());
////                if (num > 9999) {
////                    item.setCounter("9999+");
////                }
////                num = Integer.parseInt(item.getComments());
////                if (num > 999) {
////                    item.setComments("999+");
////                }
////            } else {
////                item.setCounter("0");
////                item.setComments("0");
////            }
////            StringBuilder sb = new StringBuilder(Html.fromHtml(item.getHometext().replaceAll("<.*?>|[\\r|\\n]", "")));
////            if (sb.length() > 140) {
////                item.setSummary(sb.replace(140, sb.length(), "...").toString());
////            } else {
////                item.setSummary(sb.toString());
////            }
////            if (item.getThumb().contains("thumb")) {
////                item.setLargeImage(item.getThumb().replaceAll("(\\.\\w{3,4})?_100x100|thumb/mini/", ""));
////            }
////            if (!find && item.getSid() != topSid) {
////                size++;
////            } else if (!find) {
////                find = true;
////            }
////        }
////        if (!find) {
////            size++;
////        }
////
////        if (!hasCached || listPage.getPage() == 1) {
////            hasCached = true;
////            mAdapter.setDataSet(itemList);
////            topSid = itemList.get(1).getSid();
////            showToastAndCache(itemList, size - 1);
////        } else {
////            dataSet.addAll(itemList);
////        }
////        current = listPage.getPage();
////    }
//
//    private void showToastAndCache(List<NewsItem> itemList, int size) {
//        if (size < 1) {
//            Crouton.makeText(mContext, mContext.getString(R.string.message_no_new_news), Style.CONFIRM).show();
//        } else {
//            Crouton.makeText(mContext, mContext.getString(R.string.message_new_news, size), Style.INFO).show();
//        }
//        FileCacheKit.getInstance().putAsync(getNewsType().hashCode() + "", Toolkit.getGson().toJson(itemList), "list", null);
//    }
//
//    public void setLoadFinish() {
//        if (mLoader.getLoading()) {
//            mLoader.setLoading(false);
//        }
//        mLoader.notifyDataSetChanged();
//        if (mSwipeLayout.isRefreshing()) {
//            mSwipeLayout.setRefreshing(false);
//        }
//    }
//
//    public void onReturn(int request, int response) {
////        if (response == 200) {
////            mAdapter.notifyDataSetChanged(true);
////        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        fixPos();
//    }
//
//    private void fixPos() {
//        int[] ints = helper.getInsertPixs(false);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionButton.getLayoutParams();
//        int margin = layoutParams.leftMargin;
//        layoutParams.rightMargin = margin + ints[2];
//        layoutParams.bottomMargin = margin + ints[3];
//        actionButton.setLayoutParams(layoutParams);
//    }
//
//    @Override
//    public void onRefresh() {
//        //NetKit.getInstance().getNewslistByPage(1, getNewsType(), newsPage);
//    }
//
//    public void callNewsPageLoadSuccess(List<HotCommentItem> result) {
//        for (HotCommentItem item:result){
//            Matcher hotMatcher = Configure.HOT_COMMENT_PATTERN.matcher(item.getDescription());
//            if (hotMatcher.find()) {
//                item.setDescription( hotMatcher.group(2) + " [" + hotMatcher.group(1) + "]");
//                item.setSid(Integer.parseInt(hotMatcher.group(3)));
//                item.setNewstitle(hotMatcher.group(4));
//            }
//        }
//        List<HotCommentItem> dataSet = mAdapter.getDataSet();
//        dataSet.addAll(result);
//        current++;
//    }
//}
