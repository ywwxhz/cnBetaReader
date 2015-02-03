package com.ywwxhz.hoder;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.CommentItem;

import java.util.Locale;

/**
 * CnbetaReader
 * com.ywwxhz.hoder
 * Created by 远望の无限(ywwxhz) on 2015/2/3 9:49.
 */
public class NewsCommentItemHoderView extends RelativeLayout {

    private TextView comment_name;
    private TextView comment_ref;
    private TextView comment_content;
    private TextView comment_reason;
    private TextView comment_score;
    private TextView comment_time;
    private View comment_more;

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
        this.comment_more = findViewById(R.id.comment_more);
    }

    public void showComment(CommentItem item){
        comment_name.setText(String.format(Locale.CHINA, "%s [%s]", item.getName(), item.getHost_name()));
        if (item.getRefContent().length() != 0) {
            comment_ref.setVisibility(View.VISIBLE);
            comment_ref.setText(Html.fromHtml(item.getRefContent()));
        } else {
            comment_ref.setVisibility(View.GONE);
        }
        comment_content.setText(Html.fromHtml(item.getComment()));
        comment_time.setText(item.getDate());
        String score;
        if (item.getScore() > 999) {
            score = "999+";
        } else {
            score = item.getScore() + "";
        }
        comment_score.setText(score);
        String reason;
        if (item.getReason() > 999) {
            reason = "999+";
        } else {
            reason = item.getReason() + "";
        }
        comment_reason.setText(reason);
    }
}
