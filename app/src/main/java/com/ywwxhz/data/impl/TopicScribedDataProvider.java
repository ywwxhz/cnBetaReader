package com.ywwxhz.data.impl;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ywwxhz.MyApplication;
import com.ywwxhz.activitys.TopicNewsListActivity;
import com.ywwxhz.adapters.TopicListAdapter;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.lib.database.exception.DbException;
import com.ywwxhz.lib.database.sqlite.Selector;

import java.util.ArrayList;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:28.
 */
public class TopicScribedDataProvider extends ListDataProvider<TopicItem,TopicListAdapter> {
    protected int current;
    public TopicScribedDataProvider(Activity mActivity) {
        super(mActivity);
        hasCached = false;
        setPageSize(20);
    }

    @Override
    protected TopicListAdapter newAdapter() {
        return new TopicListAdapter(getActivity(),new ArrayList<TopicItem>());
    }

    @Override
    public String getTypeKey() {
        return "subscribed";
    }

    @Override
    public String getTypeName() {
        return "已关注";
    }

    @Override
    public void loadNewData() {
        int old = current;
        try {
            current = 0;
            getAdapter().setDataSet(MyApplication.getInstance().getDbUtils().<TopicItem>findAll(
                            Selector.from(TopicItem.class).where("saved", "=", true).limit(getPageSize())
                                    .offset(current * getPageSize()).orderBy("latter", false))
            );
        } catch (DbException e) {
            current = old;
            e.printStackTrace();
        }
        if(callback!=null){
            callback.onLoadFinish();
        }
    }

    @Override
    public void loadNextData() {
        try {
            current++;
            getAdapter().getDataSet()
                    .addAll(MyApplication.getInstance().getDbUtils().<TopicItem>findAll(
                                    Selector.from(TopicItem.class).where("saved", "=", true).limit(getPageSize())
                                            .offset(current * getPageSize()).orderBy("latter",false))
                    );
        } catch (DbException e) {
            current--;
            e.printStackTrace();
        }
        if(callback!=null){
            callback.onLoadFinish();
        }
    }

    @Override
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TopicNewsListActivity.class);
                intent.putExtra(TopicNewsListActivity.TPOIC_KEY,getAdapter().getDataSetItem(i-1));
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public void loadData(boolean startup) {
        loadNewData();
    }
}
