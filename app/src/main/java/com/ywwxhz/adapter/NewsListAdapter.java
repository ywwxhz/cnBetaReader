package com.ywwxhz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.NewsItem;
import com.ywwxhz.hoder.NewsListItemHoderView;
import com.ywwxhz.lib.kits.PrefKit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class NewsListAdapter extends BaseAdapter<NewsItem> {

    private int layout;
    private boolean showLarge;
    private boolean showImage;
    private DisplayImageOptions options;
    private AnimateFirstDisplayListener listener =  new AnimateFirstDisplayListener();

    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new SimpleBitmapDisplayer());
        if (showLarge) {
            layout = R.layout.news_list_item;
            builder.showImageOnLoading(R.drawable.imagehoder)
                    .showImageOnFail(R.drawable.imagehoder_error);
        } else {
            layout = R.layout.news_list_item1;
            builder.showImageOnLoading(R.drawable.imagehoder_sm)
                    .showImageOnFail(R.drawable.imagehoder_error_sm);
        }
        options = builder.build();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        NewsListItemHoderView view;
        if (convertView == null) {
            view = (NewsListItemHoderView)infater.inflate(layout, parent, false);
        } else {
            view = (NewsListItemHoderView) convertView;
        }
        NewsItem item = getDataSetItem(position);
        view.showNews(item,showImage,showLarge,options,listener);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay&&imageView.getVisibility()==View.VISIBLE) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
