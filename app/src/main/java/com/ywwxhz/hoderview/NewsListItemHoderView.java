package com.ywwxhz.hoderview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.kits.UIKit;

/**
 * CnbetaReader
 * com.ywwxhz.hoder
 * Created by 远望の无限(ywwxhz) on 2015/2/2 22:31.
 */
public class NewsListItemHoderView extends LinearLayout {
    private TextView news_time;
    private TextView news_title;
    private TextView news_views;
    private TextView news_summary;
    private TextView news_comment;
    private ImageView news_image_large;
    private ImageView news_image_small;

    public NewsListItemHoderView(Context context) {
        super(context);
    }

    public NewsListItemHoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListItemHoderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.news_time = (TextView) findViewById(R.id.news_time);
        this.news_title = (TextView) findViewById(R.id.news_title);
        this.news_views = (TextView) findViewById(R.id.news_views);
        this.news_summary = (TextView) findViewById(R.id.news_summary);
        this.news_comment = (TextView) findViewById(R.id.news_comments);
        this.news_image_large = (ImageView) findViewById(R.id.news_image_large);
        this.news_image_small = (ImageView) findViewById(R.id.news_image_small);
        Drawable[] viewDrawables = news_views.getCompoundDrawables();
        viewDrawables[0] = UIKit.tintDrawable(viewDrawables[0],news_views.getTextColors());
        news_views.setCompoundDrawables(viewDrawables[0],null,null,null);
        Drawable[] commentDrawables = news_comment.getCompoundDrawables();
        commentDrawables[0] = UIKit.tintDrawable(commentDrawables[0],news_comment.getTextColors());
        news_comment.setCompoundDrawables(commentDrawables[0],null,null,null);
    }

    public void showNews(NewsItem item, boolean showImage, boolean showLarge, DisplayImageOptions optionsLarge, DisplayImageOptions optionsSmall, NewsListAdapter.AnimateFirstDisplayListener listener) {
        news_title.setText(item.getTitle());
        news_views.setText(item.getCounter());
        news_time.setText(item.getInputtime());
        news_comment.setText(item.getComments());
        news_summary.setText(item.getSummary());
        if (!showImage) {
            if (news_image_large.getVisibility() == VISIBLE) {
                news_image_large.setVisibility(GONE);
            }
            if (news_image_small.getVisibility() == VISIBLE) {
                news_image_small.setVisibility(GONE);
            }
        } else {
            if (showLarge) {
                if (news_image_small.getVisibility() == VISIBLE) {
                    news_image_small.setVisibility(GONE);
                }
                if (item.getLargeImage() != null) {
                    if (news_image_large.getVisibility() == GONE) {
                        news_image_large.setVisibility(VISIBLE);
                    }
                    ImageLoader.getInstance().displayImage(item.getLargeImage(), news_image_large, optionsLarge, listener);
                } else {
                    if (news_image_large.getVisibility() == VISIBLE) {
                        news_image_large.setVisibility(GONE);
                    }
                }
            } else {
                if (news_image_large.getVisibility() == VISIBLE) {
                    news_image_large.setVisibility(GONE);
                }
                if (news_image_small.getVisibility() == GONE) {
                    news_image_small.setVisibility(VISIBLE);
                }
                ImageLoader.getInstance().displayImage(item.getThumb(), news_image_small, optionsSmall);
            }
        }
    }
}
