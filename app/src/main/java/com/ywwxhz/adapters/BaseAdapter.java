package com.ywwxhz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/9/3 17:52.
 */
public abstract class BaseAdapter<E> extends android.widget.BaseAdapter {

    protected LayoutInflater infater;
    protected List<E> items;
    protected Context context;

    public BaseAdapter(Context context, List<E> items) {
        this(context);
        this.items = items;
    }

    public BaseAdapter(Context context) {
        this.context = context;
        this.infater = LayoutInflater.from(context);
    }

    public void setContext(Context context){
        this.context = context;
        this.infater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return bindViewAndData(infater, position, convertView, parent);
    }

    public List<E> getDataSet() {
        return items;
    }

    public void setDataSet(List<E> dataset) {
        this.items = dataset;
    }

    public E getDataSetItem(int postion) {
        return items.get(postion);
    }

    protected abstract View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent);

    public void notifyDataSetChanged(boolean changeConfig) {
        super.notifyDataSetChanged();
    }

}
