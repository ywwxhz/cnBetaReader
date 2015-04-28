package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.ResponseHandlerInterface;
import com.ywwxhz.activitys.ExtNewsDetailActivity;
import com.ywwxhz.activitys.NewsDetailActivity;
import com.ywwxhz.adapters.HotCommentAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.fragments.NewsDetailFragment;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.handler.BaseHttpResponseHandler;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:02.
 */
public class NetHotCommentDataProvider extends ListDataProvider<HotCommentItem,HotCommentAdapter> {

    private int current;

    private ResponseHandlerInterface newsPage = new BaseHttpResponseHandler<List<HotCommentItem>>(new TypeToken<ResponseObject<List<HotCommentItem>>>() {
    }){

        @Override
        protected void onSuccess(List<HotCommentItem> result) {
            for (HotCommentItem item:result){
                Matcher hotMatcher = Configure.HOT_COMMENT_PATTERN.matcher(item.getDescription());
                if (hotMatcher.find()) {
                    item.setFrom(hotMatcher.group(1));
                    item.setDescription(hotMatcher.group(2));
                    item.setSid(Integer.parseInt(hotMatcher.group(3)));
                    item.setNewstitle(hotMatcher.group(4));
                }
            }
            if(current == 1){
                getAdapter().setDataSet(result);
                Toolkit.showCrouton(getActivity(), getActivity().getString(R.string.message_flush_success), CroutonStyle.INFO);
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
                Intent intent;
                if(PrefKit.getBoolean(getActivity(), R.string.pref_new_detail_key, true)) {
                    intent = new Intent(getActivity(), ExtNewsDetailActivity.class);
                }else{
                    intent = new Intent(getActivity(), NewsDetailActivity.class);
                }
                NewsItem item = new NewsItem();
                HotCommentItem commentItem = getAdapter().getDataSetItem(i - 1);
                item.setSid(commentItem.getSid());
                item.setTitle(commentItem.getNewstitle());
                intent.putExtra(NewsDetailFragment.NEWS_ITEM_KEY, item);
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboardManager = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, getAdapter().getDataSetItem(position - 1).getTitle()));
                Toast.makeText(getActivity(), "评论已复制到剪贴板", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        ArrayList<HotCommentItem> items = FileCacheKit.getInstance().getAsObject(getTypeKey().hashCode() + "", "list", new TypeToken<ArrayList<HotCommentItem>>() {
        });
        if (items != null) {
            getAdapter().setDataSet(items);
            getAdapter().notifyDataSetChanged();
        }
        this.current = 1;
    }

}
