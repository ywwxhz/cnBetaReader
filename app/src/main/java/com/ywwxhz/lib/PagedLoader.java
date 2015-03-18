package com.ywwxhz.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.adapter.BaseAdapter;
import com.ywwxhz.cnbetareader.R;

public class PagedLoader implements OnScrollListener, OnClickListener {
    private TextView normalTextView;
    private TextView finallyTextView;
    private ListAdapter adapter;
    private ListView listView;
    private ProgressWheel progressBar;
    // ListView底部View
    private View moreView;
    // 最后可见条目的索引
    private int lastVisibleIndex;
    private OnLoadListener mOnLoadListener;
    private OnScrollListener mOnScrollListener;
    private boolean enable;
    private boolean isLoading;
    private Mode mode = Mode.AUTO_LOAD;

    private PagedLoader() {
    }

    public void setOnLoadListener(OnLoadListener mOnLoadListener) {
        this.mOnLoadListener = mOnLoadListener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引
        lastVisibleIndex = firstVisibleItem + visibleItemCount;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        if (enable && mode == Mode.AUTO_LOAD && !isLoading && scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == listView.getAdapter().getCount()) {
            if (mOnLoadListener != null) {
                setLoading(true);
                mOnLoadListener.onLoading(this, true);
            }
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }

    }

    private void setListView(ListView listView) {
        this.listView = listView;
    }

    private void setLoading(boolean isloading, boolean isfinall) {
        setLoading(isloading);
        if (isfinall) {
            normalTextView.setVisibility(View.GONE);
            finallyTextView.setVisibility(View.VISIBLE);
        } else {
            normalTextView.setVisibility(View.VISIBLE);
            finallyTextView.setVisibility(View.GONE);
        }
    }

    public boolean getLoading() {
        return isLoading;
    }

    public void setLoading(boolean isloading) {
        isLoading = isloading;
        if (isloading) {
            progressBar.spin();
            progressBar.setVisibility(View.VISIBLE);
            normalTextView.setVisibility(View.GONE);
        } else {
            progressBar.stopSpinning();
            normalTextView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (enable && mode == Mode.CLICK_TO_LOAD) {
            setLoading(true);
            mOnLoadListener.onLoading(this, false);
        }
    }

    public CharSequence getLoadingText() {
        return normalTextView.getText();
    }

    public void setNormalText(CharSequence title) {
        normalTextView.setText(title);
    }

    public void setNormalText(int res) {
        normalTextView.setText(res);
    }

    public void setFinally() {
        setFinally(true);
    }

    public void setFinally(boolean finall) {
        if (finall) {
            setLoading(false, true);
            enable = false;
        } else {
            setLoading(false, false);
            enable = true;
        }
    }

    public void setFinallyText(CharSequence text) {
        finallyTextView.setText(text);
    }

    public void setFinallyText(int res) {
        finallyTextView.setText(res);
    }

    private void setDisplay(boolean show) {
        if (show) {
            moreView.setVisibility(View.VISIBLE);
        } else {
            moreView.setVisibility(View.GONE);
        }
        finallyTextView.setVisibility(View.GONE);
    }

    public void notifyDataSetChanged() {
        ListAdapter adapter;
        if (this.adapter != null) {
            adapter = this.adapter;
        } else if (listView.getAdapter() != null) {
            adapter = this.listView.getAdapter();
        } else {
            throw new RuntimeException("must set adapter after notifyDataSetChanged");
        }
        ((BaseAdapter) adapter).notifyDataSetChanged();
        if (adapter.getCount() == 0) {
            setEnable(false);
        } else {
            setEnable(true);
        }
    }


    public void notifyDataSetInvalidated() {
        ListAdapter adapter;
        if (this.adapter != null) {
            adapter = this.adapter;
        } else if (listView.getAdapter() != null) {
            adapter = this.listView.getAdapter();
        } else {
            throw new RuntimeException("must set adapter after notifyDataSetChanged");
        }
        ((BaseAdapter) adapter).notifyDataSetInvalidated();
        if (adapter.getCount() == 0) {
            setEnable(false);
        } else {
            setEnable(true);
        }
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        this.listView.setAdapter(adapter);
        if (adapter == null || adapter.getCount() == 0) {
            setEnable(false);
        } else {
            // 判断是否绑定事件，如果没有任何事件绑定，不显示foot
            if (mOnLoadListener == null) {
                setEnable(false);
            } else {
                setEnable(true);
            }
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        setDisplay(enable);
    }

    private PagedLoader getPageLoader() {
        return this;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;

    }

    public enum Mode {
        CLICK_TO_LOAD, AUTO_LOAD
    }

    public interface OnLoadListener {
        public void onLoading(PagedLoader pagedLoader, boolean isAutoLoad);
    }

    public static class Builder {
        private Context mContext;
        private PagedLoader pagedLoader;

        private Builder(Context context) {
            this.mContext = context;
            pagedLoader = new PagedLoader();
            // 实例化底部布局
            pagedLoader.moreView = LayoutInflater.from(mContext).inflate(R.layout.paged_foot,
                    pagedLoader.listView, false);
            pagedLoader.normalTextView = (TextView) pagedLoader.moreView.findViewById(R.id.bt_load);
            pagedLoader.finallyTextView = (TextView) pagedLoader.moreView.findViewById(R.id.bt_finally);
            pagedLoader.progressBar = (ProgressWheel) pagedLoader.moreView.findViewById(R.id.pg);
        }

        public static Builder getInstance(Context context) {
            return new Builder(context);
        }

        public Builder setListView(ListView listView) {
            pagedLoader.setListView(listView);
            return this;
        }

        public Builder setMode(Mode mode) {
            pagedLoader.setMode(mode);
            return this;
        }

        public Builder setNormalText(CharSequence text) {
            pagedLoader.setNormalText(text);
            return this;
        }

        public Builder setNormalText(int res) {
            pagedLoader.setNormalText(res);
            return this;
        }

        public Builder setFinallyText(CharSequence text) {
            pagedLoader.setFinallyText(text);
            return this;
        }

        public Builder setFinallyText(int res) {
            pagedLoader.setFinallyText(res);
            return this;
        }

        public Builder setOnLoadListener(OnLoadListener mOnLoadListener) {
            pagedLoader.setOnLoadListener(mOnLoadListener);
            return this;
        }

        public Builder setOnScrollListener(OnScrollListener mOnScrollListener) {
            pagedLoader.setOnScrollListener(mOnScrollListener);
            return this;
        }

        public Builder setFinallyText(String text) {
            pagedLoader.setFinallyText(text);
            return this;
        }

        public PagedLoader builder() {
            if (pagedLoader.listView == null) {
                throw new RuntimeException("must set listView before builder()");
            } else if (pagedLoader.listView.getAdapter() != null) {
                throw new RuntimeException("must set footview before set adapter");
            }
            // 加上底部View，注意要放在setAdapter方法前
            pagedLoader.listView.addFooterView(pagedLoader.moreView, null, false);
            pagedLoader.listView.setFooterDividersEnabled(false);
            pagedLoader.normalTextView.setOnClickListener(pagedLoader);
            if (pagedLoader.mode == Mode.AUTO_LOAD) {
                // 绑定监听器
                pagedLoader.listView.setOnScrollListener(pagedLoader.getPageLoader());
            }
            pagedLoader.setEnable(false);
            return pagedLoader;
        }
    }
}
