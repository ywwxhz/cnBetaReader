package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.NewsItem;
import com.ywwxhz.lib.kits.PrefKit;

import java.util.List;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NewsListAdapter extends BaseAdapter<NewsItem> {

    private int layout;
    private boolean showLarge;
    private boolean showImage;
    private DisplayImageOptions options;

    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer());
        if (showLarge) {
            layout = R.layout.news_list_item;
            builder.showImageOnLoading(R.drawable.imagehoder)
                    .showImageForEmptyUri(R.drawable.imagehoder_error)
                    .showImageOnFail(R.drawable.imagehoder_error);
        } else {
            layout = R.layout.news_list_item1;
            builder.showImageOnLoading(R.drawable.imagehoder_sm)
                    .showImageForEmptyUri(R.drawable.imagehoder_sm)
                    .showImageOnFail(R.drawable.imagehoder_error_sm);
        }
        options = builder.build();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        ViewHoder hoder;
        View view;
        if (convertView == null) {
            view = infater.inflate(layout, parent, false);
            hoder = new ViewHoder(view);
            view.setTag(hoder);
        } else {
            view = convertView;
            hoder = (ViewHoder) convertView.getTag();
        }
        NewsItem item = getDataSetItem(position);
        hoder.news_title.setText(item.getTitle());
        hoder.news_views.setText(item.getCounter());
        hoder.news_time.setText(item.getInputtime());
        hoder.news_comment.setText(item.getComments());
        if (showImage) {
            if (showLarge) {
                if (item.getLargeImage() != null) {
                    hoder.news_image_hoder.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(item.getLargeImage(), hoder.news_image, options);
                } else {
                    hoder.news_image_hoder.setVisibility(View.GONE);
                }
            } else {
                hoder.news_image_hoder.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(item.getThumb(), hoder.news_image, options);
            }
        } else {
            hoder.news_image_hoder.setVisibility(View.GONE);
        }
        hoder.news_summary.setText(item.getSummary());
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
    }

    class ViewHoder {
        TextView news_time;
        TextView news_title;
        TextView news_views;
        TextView news_summary;
        TextView news_comment;
        ImageView news_image;
        View news_image_hoder;

        public ViewHoder(View view) {
            this.news_time = (TextView) view.findViewById(R.id.news_time);
            this.news_title = (TextView) view.findViewById(R.id.news_title);
            this.news_views = (TextView) view.findViewById(R.id.news_views);
            this.news_summary = (TextView) view.findViewById(R.id.news_summary);
            this.news_comment = (TextView) view.findViewById(R.id.news_comments);
            this.news_image = (ImageView) view.findViewById(R.id.news_image);
            this.news_image_hoder = view.findViewById(R.id.news_image_hoder);
        }
    }
}
