package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.HotCommentAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.handler.BaseHttpResponseHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.processers.NewsDetailProcesserImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:02.
 */
public class NetHotCommentDataProvider extends ListDataProvider<HotCommentAdapter> {

    private int current;

    private ResponseHandlerInterface newsPage = new BaseHttpResponseHandler<List<HotCommentItem>>(new TypeToken<ResponseObject<List<HotCommentItem>>>() {
    }){

        @Override
        protected void onSuccess(List<HotCommentItem> result) {
            for (HotCommentItem item:result){
                Matcher hotMatcher = Configure.HOT_COMMENT_PATTERN.matcher(item.getDescription());
                if (hotMatcher.find()) {
                    item.setDescription(hotMatcher.group(2) + " [" + hotMatcher.group(1) + "]");
                    item.setSid(Integer.parseInt(hotMatcher.group(3)));
                    item.setNewstitle(hotMatcher.group(4));
                }
            }
            if(current == 1){
                getAdapter().setDataSet(result);
                Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_load_success), Style.INFO);
                FileCacheKit.getInstance().putAsync(getTypeKey().hashCode() + "", Toolkit.getGson().toJson(result), "list", null);
            }else{
                getAdapter().getDataSet().addAll(result);
            }
        }

        @Override
        protected Activity getActivity() {
            return NetHotCommentDataProvider.this.getActivity();
        }

        @Override
        public void onFinish() {
            if(callback!=null){
                callback.onLoadFinish();
            }
        }
    };

    public NetHotCommentDataProvider(Activity mActivity) {
        super(mActivity);
        hasCached = false;
    }

    @Override
    protected HotCommentAdapter newAdapter() {
        return new HotCommentAdapter(getActivity(),new ArrayList<HotCommentItem>());
    }

    @Override
    public String getTypeKey() {
        return "jhcomment";
    }

    @Override
    public String getTypeName() {
        return "精华评论";
    }

    @Override
    public void loadNewData() {
        current = 1;
        NetKit.getInstance().getNewslistByPage(current, getTypeKey(), newsPage);
    }

    @Override
    public void loadNextData() {
        current++;
        NetKit.getInstance().getNewslistByPage(current, getTypeKey(), newsPage);
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                NewsItem item = new NewsItem();
                HotCommentItem commentItem = getAdapter().getDataSetItem(i - 1);
                item.setSid(commentItem.getSid());
                item.setTitle(commentItem.getNewstitle());
                intent.putExtra(NewsDetailProcesserImpl.NEWS_ITEM_KEY, item);
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        ArrayList<HotCommentItem> items = FileCacheKit.getInstance().getAsObject(getTypeKey().hashCode() + "", "list", new TypeToken<ArrayList<HotCommentItem>>() {
        });
        if (items != null) {
            hasCached = true;
            getAdapter().setDataSet(items);
            getAdapter().notifyDataSetChanged();
        } else {
            this.hasCached = false;
        }
        this.current = 1;
    }

}
