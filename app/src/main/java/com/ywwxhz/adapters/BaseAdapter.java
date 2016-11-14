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

    /**
     * 设置
     * @param context
     */
    public void setContext(Context context){
        this.context = context;
        if(infater==null) {
            this.infater = LayoutInflater.from(context);
        }
    }

    /**
     * 获取数量
     * @return
     */
    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * 获取元素
     * @param position index
     * @return 元素
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     * 获取元素的ID
     * @param position index
     * @return
     */
    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return bindViewAndData(infater, position, convertView, parent);
    }

    /**
     * 获取结果集
     * @return
     */
    public List<E> getDataSet() {
        return items;
    }

    /**
     * 设置结果集
     * @param dataset
     */
    public void setDataSet(List<E> dataset) {
        this.items = dataset;
    }

    /**
     * 获取结果集中的项
     * @param postion index
     * @return 元素
     */
    public E getDataSetItem(int postion) {
        return items.get(postion);
    }

    /**
     * 绑定视图与数据
     * @param infater
     * @param position index
     * @param convertView 缓存的视图
     * @param parent 父控件
     * @return 视图
     */
    protected abstract View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent);

    /**
     * 通知结果集发生改变
     * @param changeConfig
     */
    public void notifyDataSetChanged(boolean changeConfig) {
        super.notifyDataSetChanged();
    }

}
