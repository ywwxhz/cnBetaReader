package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.entitys.NewsListObject;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.handler.BaseCallback;
import com.ywwxhz.lib.handler.BaseResponseObjectResponse;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 21:19.
 */
public abstract class NetNewsListDataProvider extends BaseNewsListDataProvider<NewsListAdapter> {

    private int topSid;
    private int secondSid;
    private int thirdSid;
    private int current;

    private BaseResponseObjectResponse newsPage = new BaseResponseObjectResponse<NewsListObject>(
            new TypeToken<ResponseObject<NewsListObject>>() {
            }) {
        private int size = 0;
        private List<NewsItem> itemList;

        @Override
        public ResponseObject<NewsListObject> convertSuccess(Response response) throws Exception {
            int offsetFirst = -1;
            int offsetSecond = -1;
            int offsetThird = -1;
            boolean findFirst = false;
            boolean findSecond = false;
            boolean findThird = false;
            ResponseObject<NewsListObject> responseObject = super.convertSuccess(response);
            boolean calNew = responseObject.getResult().getPage() == 1;
            itemList = responseObject.getResult().getList();
            for (NewsItem item : itemList) {
                if (item.getCounter() != null && item.getComments() != null) {
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
                item.setTitle(item.getTitle().replaceAll("<.*?>", ""));
                StringBuilder sb = new StringBuilder(
                        Html.fromHtml(item.getHometext().replaceAll("<.*?>|[\\r|\\n]", "")));
                if (sb.length() > 140) {
                    item.setSummary(sb.replace(140, sb.length(), "...").toString());
                } else {
                    item.setSummary(sb.toString());
                }
                if (item.getThumb().contains("thumb")) {
                    item.setLargeImage(
                            item.getThumb().replaceAll("(\\.\\w{3,4})?_100x100|thumb/mini/", ""));
                }
                if (calNew) {
                    if (!findFirst) {
                        if (item.getSid() == topSid) {
                            findFirst = true;
                        }
                        offsetFirst++;
                    }
                    if (!findSecond) {
                        if (item.getSid() == secondSid) {
                            findSecond = true;
                        }
                        offsetSecond++;
                    }
                    if (!findThird) {
                        if (item.getSid() == thirdSid) {
                            findThird = true;
                        }
                        offsetThird++;
                    }
                }
            }
            if (calNew) {
                /**
                 * 判断新增新闻逻辑<br/>
                 * 如果topSid为0，列表为首次加载，新增新闻数量为加载的数量<br/>
                 * 如果topSid不为0，程序不是首次加载<br/>
                 *      topSid的偏移量大于0则判断新闻数量通过offsetFirst和findFirst决定<br/>
                 *      topSid的偏移量为0有两种可能，第一种是有新闻置顶，还有一种是没有新的新闻。
                 *      需要通过offsetThird和offsetSecond决定新闻的数量
                 */
                if (topSid != 0) {
                    if (offsetFirst > 0) {
                        if (findFirst) {
                            size = offsetFirst;
                        } else {
                            size = itemList.size();
                        }
                    } else {
                        if (offsetThird - offsetSecond > 1) {
                            size = offsetThird - 1;
                        } else {
                            size = offsetSecond - 1;
                        }
                    }
                } else {
                    size = itemList.size();
                }
                Log.d(NetNewsListDataProvider.class.getName(),
                      String.format(Locale.CHINA, "topSid:%d\tsecondSid:%d\tthirdSid:%d"
                              , topSid, secondSid, thirdSid));
                Log.d(NetNewsListDataProvider.class.getName(),
                      String.format(Locale.CHINA, "offsetFirst:%d\toffsetSecond:%d\toffsetThird:%d"
                              , offsetFirst, offsetSecond, offsetThird));
            }
            return responseObject;
        }

        /**
         * @param result
         */
        @Override
        protected void onSuccess(NewsListObject result) {
            List<NewsItem> dataSet = getAdapter().getDataSet();
            if (!hasCached || result.getPage() == 1) {
                hasCached = true;
                getAdapter().setDataSet(itemList);
                if (itemList.size() > 3) {
                    topSid = itemList.get(0).getSid();
                    secondSid = itemList.get(1).getSid();
                    thirdSid = itemList.get(2).getSid();
                }
                showToastAndCache(itemList, size);
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
        public void onAfter(
                @Nullable ResponseObject<NewsListObject> newsListObjectResponseObject,
                @Nullable Exception e) {
            if (callback != null) {
                callback.onLoadFinish(40);
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

    public void makeRequest(int page, String type, BaseCallback handlerInterface) {
        NetKit.getNewslistByPage(getActivity(), page, type, handlerInterface);
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                NewsItem item = getAdapter().getDataSetItem(i - 1);
                //                item.setCache(true);
                intent.putExtra(NewsDetailFragment.NEWS_SID_KEY, item.getSid());
                intent.putExtra(NewsDetailFragment.NEWS_TITLE_KEY, item.getTitle());

                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        ArrayList<NewsItem> newsList = FileCacheKit.getInstance()
                .getAsObject(getTypeKey().hashCode() + "", "list",
                             new TypeToken<ArrayList<NewsItem>>() {
                             });
        if (newsList != null && newsList.size() > 2) {
            hasCached = true;
            topSid = newsList.get(0).getSid();
            secondSid = newsList.get(1).getSid();
            thirdSid = newsList.get(2).getSid();
            getAdapter().setDataSet(newsList);
            getAdapter().notifyDataSetChanged();
        } else {
            this.hasCached = false;
        }
        this.current = 1;
    }

    /**
     * 显示提示并缓存数据
     *
     * @param itemList
     * @param size
     */
    private void showToastAndCache(List<NewsItem> itemList, int size) {
        if (size < 1) {
            Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_no_new_news),
                                CroutonStyle.CONFIRM);
        } else {
            Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_new_news, size),
                                CroutonStyle.INFO);
        }
        FileCacheKit.getInstance()
                .putAsync(getTypeKey().hashCode() + "", Toolkit.getGson().toJson(itemList), "list",
                          null);
    }
}
