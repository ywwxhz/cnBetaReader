package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ywwxhz on 2014/9/3.
 */
public abstract class BaseAdapter<E> extends android.widget.BaseAdapter {

    protected final LayoutInflater infater;
    protected List<E> items;

    public BaseAdapter(Context context, List<E> items) {
        infater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    public BaseAdapter(Context context) {
        infater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


}
