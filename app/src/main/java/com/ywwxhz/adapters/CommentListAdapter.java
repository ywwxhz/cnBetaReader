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

import java.util.ArrayList;
import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/2 17:52.
 */
public class CommentListAdapter extends BaseAdapter<CommentItem> {
    private String token;
    private boolean reverse;
    private boolean showHot = false;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    private List<CommentItem> hotComment;

    public CommentListAdapter(Context context, List<CommentItem> items) {
        super(context, items);
        hotComment = new ArrayList<>();
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
        view.showComment(item, token, this, mDrawableBuilder, mColorGenerator);
        return view;
    }

    public void setHotComment(List<CommentItem> hotComment) {
        this.hotComment = hotComment;
    }

    private CommentItem getDataSetItemExt(int pos) {
        if (showHot) {
            return hotComment.get(pos);
        } else {
            return items.get(pos);
        }
    }

    @Override
    public int getCount() {
        if (showHot) {
            return hotComment != null ? hotComment.size() : 0;
        } else {
            return items != null ? items.size() : 0;
        }
    }

    @Override
    public CommentItem getDataSetItem(int postion) {
        if (reverse) {
            return getDataSetItemExt(postion);
        } else {
            return getDataSetItemExt(getCount() - 1 - postion);
        }
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

    public boolean isShowHot() {
        return showHot;
    }

    public void setShowHot(boolean showHot) {
        this.showHot = showHot;
    }
}
