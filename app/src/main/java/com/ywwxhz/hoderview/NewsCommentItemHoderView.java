package com.ywwxhz.hoderview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.CommentItem;
import com.ywwxhz.lib.SpannableStringUtils;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

/**
 * CnbetaReader com.ywwxhz.hoder Created by 远望の无限(ywwxhz) on 2015/2/3 9:49.
 */
public class NewsCommentItemHoderView extends RelativeLayout {
    private TextView comment_name;
    private TextView comment_ref;
    private TextView comment_content;
    private TextView comment_reason;
    private TextView comment_score;
    private TextView comment_time;
    private ImageView comment_image;
    private TextView comment_from;
    private boolean showEmoji;
    private CommentItem item;
    private String token;
    private BaseAdapter adapter;

    public NewsCommentItemHoderView(Context context) {
        super(context);
    }

    public NewsCommentItemHoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsCommentItemHoderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.comment_name = (TextView) findViewById(R.id.comment_name);
        this.comment_ref = (TextView) findViewById(R.id.comment_ref);
        this.comment_content = (TextView) findViewById(R.id.comment_content);
        this.comment_reason = (TextView) findViewById(R.id.comment_reason);
        this.comment_score = (TextView) findViewById(R.id.comment_score);
        this.comment_time = (TextView) findViewById(R.id.comment_time);
        this.comment_image = (ImageView) findViewById(R.id.comment_image);
        this.comment_from = (TextView) findViewById(R.id.comment_from);
        showEmoji = PrefKit.getBoolean(getContext(), R.string.pref_show_emoji_key, true);
        if (ThemeManger.isNightTheme(getContext())) {
            this.comment_ref.setBackgroundResource(R.drawable.ref_background_night);
        } else {
            this.comment_ref.setBackgroundResource(R.drawable.ref_background);
        }
        Drawable[] reasonDrawables = comment_reason.getCompoundDrawables();
        reasonDrawables[0] = UIKit.tintDrawable(reasonDrawables[0], comment_reason.getCurrentTextColor());
        comment_reason.setCompoundDrawables(reasonDrawables[0], null, null, null);
        Drawable[] scoreDrawables = comment_score.getCompoundDrawables();
        scoreDrawables[0] = UIKit.tintDrawable(scoreDrawables[0], comment_score.getCurrentTextColor());
        comment_score.setCompoundDrawables(scoreDrawables[0], null, null, null);
    }

    public void showComment(CommentItem item, String token, BaseAdapter adapter,
            TextDrawable.IBuilder drawableBuilder, ColorGenerator colorGenerator) {
        comment_name.setText(item.getName());
        this.token = token;
        this.item = item;
        this.adapter = adapter;
        if (item.getRefContent().length() != 0) {
            if (comment_ref.getVisibility() == GONE) {
                comment_ref.setVisibility(View.VISIBLE);
            }
            if (showEmoji) {
                comment_ref.setText(
                        SpannableStringUtils.span(getContext(), Html.fromHtml(item.getRefContent()).toString()));
            } else {
                comment_ref.setText(Html.fromHtml(item.getRefContent()).toString());
            }
        } else {
            if (comment_ref.getVisibility() == VISIBLE) {
                comment_ref.setVisibility(View.GONE);
            }
        }
        comment_image.setImageDrawable(drawableBuilder.build(String.valueOf(item.getName().charAt(0)),
                colorGenerator.getColor(item.getTid())));
        if (showEmoji) {
            comment_content
                    .setText(SpannableStringUtils.span(getContext(), Html.fromHtml(item.getComment()).toString()));
        } else {
            comment_content.setText(Html.fromHtml(item.getComment()).toString());
        }
        comment_time.setText(item.getDate());
        comment_from.setText(item.getHost_name());
        comment_score.setText(item.getScore() > 999 ? "999+" : item.getScore() + "");
        comment_reason.setText(item.getReason() > 999 ? "999+" : item.getReason() + "");
    }
}
