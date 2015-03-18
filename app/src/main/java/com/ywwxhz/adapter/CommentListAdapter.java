package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.CommentItem;
import com.ywwxhz.hoder.NewsCommentItemHoderView;
import com.ywwxhz.view.textdrawable.TextDrawable;
import com.ywwxhz.view.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class CommentListAdapter extends BaseAdapter<CommentItem> {
    private boolean enable;
    private String token;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    public CommentListAdapter(Context context, List<CommentItem> items) {

        super(context, items);
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        NewsCommentItemHoderView view;
        if (convertView == null) {
            view = (NewsCommentItemHoderView) infater.inflate(R.layout.news_comment_item, parent, false);
        } else {
            view = (NewsCommentItemHoderView) convertView;
        }
        CommentItem item = getDataSetItem(position);
        view.showComment(item, token, this, enable,mDrawableBuilder,mColorGenerator);
        return view;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
