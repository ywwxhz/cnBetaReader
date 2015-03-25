package com.ywwxhz.hoderview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 15-3-23 17:54.
 */
public class NewsHotCommentItemHoderView extends RelativeLayout {
    private TextView mCommentContent;
    private ImageView mCommentImage;
    private TextView mCommentName;
    private TextView mCommentFrom;

    public NewsHotCommentItemHoderView(Context context) {
        super(context);
    }

    public NewsHotCommentItemHoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsHotCommentItemHoderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCommentContent = (TextView) findViewById(R.id.comment_content);
        mCommentImage = (ImageView) findViewById(R.id.comment_image);
        mCommentName = (TextView) findViewById(R.id.comment_name);
        mCommentFrom = (TextView) findViewById(R.id.comment_from);
    }

    public void showComment(HotCommentItem item, TextDrawable.IBuilder mDrawableBuilder, ColorGenerator mColorGenerator){
        mCommentContent.setText(item.getTitle());
        mCommentFrom.setText(item.getDescription());
        mCommentImage.setImageDrawable(mDrawableBuilder.build(String.valueOf(item.getNewstitle().charAt(0)), mColorGenerator.getColor(item.getNewstitle())));
        mCommentName.setText(item.getNewstitle());
    }
}
