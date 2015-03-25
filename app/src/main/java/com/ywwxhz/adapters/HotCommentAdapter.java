package com.ywwxhz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.hoderview.NewsHotCommentItemHoderView;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 15-3-23 17:53.
 */
public class HotCommentAdapter extends BaseAdapter<HotCommentItem> {

    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;

    public HotCommentAdapter(Context context,List<HotCommentItem> items) {
        super(context,items);
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        NewsHotCommentItemHoderView view;
        if (convertView == null) {
            view = (NewsHotCommentItemHoderView) infater.inflate(R.layout.hot_comment_item, parent, false);
        } else {
            view = (NewsHotCommentItemHoderView) convertView;
        }
        HotCommentItem item = getDataSetItem(position);
        view.showComment(item,mDrawableBuilder,mColorGenerator);
        return view;
    }
}
