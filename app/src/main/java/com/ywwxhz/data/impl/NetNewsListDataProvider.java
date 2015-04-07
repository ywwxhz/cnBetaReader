package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.entitys.NewsListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.handler.BaseHttpResponseHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 21:19.
 */
public abstract class NetNewsListDataProvider extends BaseNewsListDataProvider<NewsListAdapter> {

    private int topSid;
    private int current;

    private ResponseHandlerInterface newsPage = new BaseHttpResponseHandler<NewsListObject>(new TypeToken<ResponseObject<NewsListObject>>() {
    }){

        @Override
        protected void onSuccess(NewsListObject result) {
            List<NewsItem> itemList = result.getList();
            List<NewsItem> dataSet = getAdapter().getDataSet();
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
                getAdapter().setDataSet(itemList);
                if(itemList.size()>2) {
                    topSid = itemList.get(1).getSid();
                }
                showToastAndCache(itemList, size - 1);
            } else {
                dataSet.addAll(itemList);
            }
            current = result.getPage();
        }

        @Override
        protected Activity getActivity() {
            return NetNewsListDataProvider.this.getActivity();
        }

        @Override
        public void onFinish() {
            if(callback!=null){
                callback.onLoadFinish();
            }
        }
    };

    public NetNewsListDataProvider(Activity mActivity) {
        super(mActivity);
        hasCached = false;
    }

    @Override
    public NewsListAdapter newAdapter() {
        return new NewsListAdapter(getActivity(), new ArrayList<NewsItem>());
    }

    @Override
    public void loadNewData() {
        makeRequest(1, getTypeKey(), newsPage);
    }

    @Override
    public void loadNextData() {
        makeRequest(current + 1, getTypeKey(), newsPage);
    }

    public void makeRequest(int page, String type, ResponseHandlerInterface handlerInterface){
        NetKit.getInstance().getNewslistByPage(page, type, handlerInterface);
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra(NewsDetailActivity.NEWS_ITEM_KEY, getAdapter().getDataSetItem(i - 1));
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        ArrayList<NewsItem> newsList = FileCacheKit.getInstance().getAsObject(getTypeKey().hashCode() + "", "list", new TypeToken<ArrayList<NewsItem>>() {
        });
        if (newsList != null) {
            hasCached = true;
            topSid = newsList.get(1).getSid();
            getAdapter().setDataSet(newsList);
            getAdapter().notifyDataSetChanged();
        } else {
            this.hasCached = false;
        }
        this.current = 1;
    }

    private void showToastAndCache(List<NewsItem> itemList, int size) {
        if (size < 1) {
            Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_no_new_news), CroutonStyle.CONFIRM);
        } else {
            Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_new_news, size), CroutonStyle.INFO);
        }
        FileCacheKit.getInstance().putAsync(getTypeKey().hashCode() + "", Toolkit.getGson().toJson(itemList), "list", null);
    }
}
