package com.ywwxhz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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

    private static final String TAG = NewsListAdapter.class.getSimpleName();
    private boolean showLarge;
    private boolean showImage;
    private DisplayImageOptions optionsLarge;
    private DisplayImageOptions optionsSmall;
    private AnimateFirstDisplayListener listener =  new AnimateFirstDisplayListener();

    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        optionsLarge = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.imagehoder)
                .showImageOnFail(R.drawable.imagehoder_error)
                .displayer(new SimpleBitmapDisplayer()).build();
        optionsSmall = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.imagehoder_sm)
                .showImageOnFail(R.drawable.imagehoder_error_sm)
                .displayer(new SimpleBitmapDisplayer()).build();

    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        NewsListItemHoderView view = (NewsListItemHoderView) convertView;
        if (view == null) {
            view = new NewsListItemHoderView(context,infater);
            Log.d(TAG,"new View "+view.hashCode());
        }
        NewsItem item = getDataSetItem(position);
        view.showNews(item,showImage,showLarge,optionsLarge,optionsSmall,listener);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetInvalidated() {
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        super.notifyDataSetInvalidated();
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
