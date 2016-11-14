package com.ywwxhz.data;

import android.app.Activity;
import android.widget.AdapterView;

import com.ywwxhz.adapters.BaseAdapter;

import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 20:33.
 */
public abstract class ListDataProvider<DataType, DataAdapter extends BaseAdapter<DataType>>
        extends BaseDataProvider<List<DataType>> {
    private DataAdapter adapter;
    private int minPageSize;

    private int pageSize;

    protected boolean hasCached;

    public ListDataProvider(Activity activity) {
        super(activity);
    }

    /**
     * 获取适配器
     *
     * @return {@link BaseAdapter}
     */
    public DataAdapter getAdapter() {
        if (adapter == null) {
            adapter = newAdapter();
        }
        return adapter;
    }

    /**
     * 设置Activity
     *
     * @param activity
     */
    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        getAdapter().setContext(activity);
    }

    /**
     * 创建适配器
     *
     * @return {@link DataAdapter}
     */
    protected abstract DataAdapter newAdapter();

    /**
     * 获取缓存Key
     *
     * @return
     */
    public abstract String getTypeKey();

    /**
     * 获取缓存名称
     *
     * @return
     */
    public abstract String getTypeName();

    /**
     * 加载新数据
     */
    public abstract void loadNewData();

    /**
     * 加载分页后的数据
     */
    public abstract void loadNextData();

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return null;
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return null;
    }

    /**
     * 是否已缓存
     *
     * @return
     */
    public boolean isCached() {
        return hasCached;
    }

    /**
     * 获得最小页面记录数
     *
     * @return
     */
    public int getMinPageSize() {
        return minPageSize;
    }

    /**
     * 设置最小页面记录数
     *
     * @param pageSize
     */
    public void setMinPageSize(int pageSize) {
        this.minPageSize = pageSize;
    }

    /**
     * 设置页面大小
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.minPageSize = this.pageSize = pageSize;
    }

    /**
     * 获得页面大小
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }
}
