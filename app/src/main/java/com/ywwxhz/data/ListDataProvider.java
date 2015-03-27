package com.ywwxhz.data;

import android.app.Activity;
import android.widget.AdapterView;

import com.ywwxhz.adapters.BaseAdapter;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 20:33.
 */
public abstract class ListDataProvider<DataAdapter extends BaseAdapter> extends BaseDataProvider {
    private DataAdapter adapter;
    private Activity mActivity;
    private int pageSize;

    protected boolean hasCached;

    public ListDataProvider(Activity activity) {
        super(activity);
    }

    public DataAdapter getAdapter() {
        if(adapter==null){
            adapter = newAdapter();
        }
        return adapter;
    }

    protected abstract DataAdapter newAdapter();

    public abstract String getTypeKey() ;

    public abstract String getTypeName();

    public abstract void loadNewData();

    public abstract void loadNextData();

    public abstract AdapterView.OnItemClickListener getOnItemClickListener();

    public boolean isCached() {
        return hasCached;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
