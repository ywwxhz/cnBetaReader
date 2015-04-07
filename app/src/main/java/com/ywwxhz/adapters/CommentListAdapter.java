package com.ywwxhz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.hoderview.NewsCommentItemHoderView;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:52.
 */
public class CommentListAdapter extends BaseAdapter<CommentItem> {
    private boolean enable;
    private String token;
    private boolean reverse;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    public CommentListAdapter(Context context, List<CommentItem> items) {
        super(context, items);
        mDrawableBuilder = TextDrawable.builder().round();
        reverse = PrefKit.getBoolean(context, R.string.pref_reverse_key, true);
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

    @Override
    public CommentItem getDataSetItem(int postion) {
        if(reverse){
            return super.getDataSetItem(postion);
        }else {
            return items.get(getCount() - 1 - postion);
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isReverse() {
        return reverse;
    }
}
