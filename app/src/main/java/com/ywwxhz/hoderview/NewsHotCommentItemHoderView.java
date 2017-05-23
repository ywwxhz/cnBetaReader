package com.ywwxhz.hoderview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.HotCommentItem;
import com.ywwxhz.lib.SpannableStringUtils;
import com.ywwxhz.lib.kits.PrefKit;
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
    private TextView mNewsTitle;
    private boolean showEmoji;

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
        mNewsTitle = (TextView) findViewById(R.id.news_title);
        showEmoji = PrefKit.getBoolean(getContext(),R.string.pref_show_emoji_key,true);
    }

    public void showComment(HotCommentItem item, TextDrawable.IBuilder mDrawableBuilder, ColorGenerator mColorGenerator){
        if(showEmoji) {
            mCommentContent.setText(SpannableStringUtils.span(getContext(), item.getTitle()));
        }else{
            mCommentContent.setText(item.getTitle());
        }
        mCommentFrom.setText(item.getFrom());
        mCommentImage.setImageDrawable(mDrawableBuilder.build(String.valueOf(item.getDescription().charAt(0)), mColorGenerator.getColor(item.getTitle())));
        mCommentName.setText(item.getDescription());
        mNewsTitle.setText(item.getNewstitle());
    }
}
