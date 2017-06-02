package com.ywwxhz.processers;

import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ywwxhz.adapters.CommentListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.NewsCommentProvider;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.fragments.AddNewCommentFragment;
import com.ywwxhz.lib.kits.Toolkit;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class NewsCommentProcesser extends BaseListProcesser<CommentItem,NewsCommentProvider> {
    private TextView message;
    private boolean reverse;

    public NewsCommentProcesser(NewsCommentProvider provider) {
        super(provider);
    }

    public void setParams(int sid, String sn){
        provider.setSid(sid);
        provider.setSn(sn);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        this.message = (TextView) view.findViewById(R.id.message);
        this.message.setClickable(true);
        //this.actionButton.attachToListView(getListView());
        this.actionButton.setImageResource(R.mipmap.ic_edit);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewCommentFragment fragment = AddNewCommentFragment.getInstance(provider.getSid(), "0", provider.getToken());
                fragment.show(mActivity.getFragmentManager(), "new comment");
            }
        });
        this.actionButton.setScaleX(0);
        this.actionButton.setScaleY(0);
        provider.setMessage(message);
        provider.setActionButton(actionButton);
        provider.setListView(getListView());
        provider.setSwipeLayout(getSwipeLayout());
    }

    @Override
    public void loadData(boolean startup) {
        Toolkit.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getSwipeLayout().setRefreshing(true);
                makeRequest();
            }
        }, startup?400:0);
    }

    private void makeRequest() {
        this.message.setVisibility(View.GONE);
        provider.loadNewData();
    }

    @Override
    public void onLoadFinish(int size) {
        super.onLoadFinish(size);
        if(getProvider().getAdapter().getCount()!=0){
            getLoader().setFinally();
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onRefresh() {
        makeRequest();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(provider.getAdapter().getCount()>0) {
            inflater.inflate(R.menu.menu_comment_list, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_reverse){
            CommentListAdapter adapter = getProvider().getAdapter();
            adapter.setReverse(!adapter.isReverse());
            adapter.notifyDataSetChanged();
            return true;
        }else if(item.getItemId() == R.id.menu_hot_comment){
            CommentListAdapter adapter = getProvider().getAdapter();
            adapter.setShowHot(!adapter.isShowHot());

            if(adapter.isShowHot()) {
                setHeadViewText("热门评论");
                item.setTitle("全部评论");
                item.setIcon(R.drawable.ic_normal_comment);
                reverse = adapter.isReverse();
                adapter.setReverse(true);
            }else{
                setHeadViewText("全部评论");
                adapter.setReverse(reverse);
                item.setTitle("热门评论");
                item.setIcon(R.drawable.ic_hot_comment);
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
