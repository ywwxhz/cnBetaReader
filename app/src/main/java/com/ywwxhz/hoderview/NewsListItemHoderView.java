package com.ywwxhz.hoderview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.adapters.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;

/**
 * CnbetaReader
 * com.ywwxhz.hoder
 * Created by 远望の无限(ywwxhz) on 2015/2/2 22:31.
 */
public class NewsListItemHoderView extends RelativeLayout {
    private TextView news_time;
    private TextView news_title;
    private TextView news_views;
    private TextView news_summary;
    private TextView news_comment;
    private ImageView news_image_large;
    private ImageView news_image_small;

    public NewsListItemHoderView(Context context, LayoutInflater infater) {
        super(context);
        infater.inflate(R.layout.news_list_item, this);
        this.news_time = (TextView) findViewById(R.id.news_time);
        this.news_title = (TextView) findViewById(R.id.news_title);
        this.news_views = (TextView) findViewById(R.id.news_views);
        this.news_summary = (TextView) findViewById(R.id.news_summary);
        this.news_comment = (TextView) findViewById(R.id.news_comments);
        this.news_image_large = (ImageView) findViewById(R.id.news_image_large);
        this.news_image_small = (ImageView) findViewById(R.id.news_image_small);
        setBackgroundResource(R.drawable.list_item_background);
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
