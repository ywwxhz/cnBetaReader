package com.ywwxhz.hoder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.adapter.NewsListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.NewsItem;

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
    private ImageView news_image;

    public NewsListItemHoderView(Context context) {
        super(context);
    }

    public NewsListItemHoderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListItemHoderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.news_time = (TextView) findViewById(R.id.news_time);
        this.news_title = (TextView) findViewById(R.id.news_title);
        this.news_views = (TextView) findViewById(R.id.news_views);
        this.news_summary = (TextView) findViewById(R.id.news_summary);
        this.news_comment = (TextView) findViewById(R.id.news_comments);
        this.news_image = (ImageView) findViewById(R.id.news_image);
    }

    public void showNews(NewsItem item,boolean showImage,boolean showLarge,DisplayImageOptions options,NewsListAdapter.AnimateFirstDisplayListener listener){
        news_title.setText(item.getTitle());
        news_views.setText(item.getCounter());
        news_time.setText(item.getInputtime());
        news_comment.setText(item.getComments());
        if (showImage) {
            if (showLarge) {
                if (item.getLargeImage() != null) {
                    news_image.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(item.getLargeImage(), news_image, options,listener);
                } else {
                    news_image.setVisibility(View.GONE);
                }
            } else {
                news_image.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(item.getThumb(), news_image, options);
            }
        } else {
            news_image.setVisibility(View.GONE);
        }
        news_summary.setText(item.getSummary());
    }
}
