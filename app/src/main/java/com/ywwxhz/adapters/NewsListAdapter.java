package com.ywwxhz.adapters;

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
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.hoderview.NewsListItemHoderView;
import com.ywwxhz.lib.kits.PrefKit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:53.
 */
public class NewsListAdapter extends BaseAdapter<NewsItem> {

    private static final String TAG = NewsListAdapter.class.getSimpleName();
    private boolean showLarge;
    private boolean showImage;
    private DisplayImageOptions optionsLarge;
    private DisplayImageOptions optionsSmall;
    private AnimateFirstDisplayListener listener = new AnimateFirstDisplayListener();

    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), true);
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
            view = (NewsListItemHoderView) infater.inflate(R.layout.news_list_item,parent,false);
        }
        NewsItem item = getDataSetItem(position);
        view.showNews(item, showImage, showLarge, optionsLarge, optionsSmall, listener);
        return view;
    }

    @Override
    public void notifyDataSetChanged(boolean changeConfig) {
        if(changeConfig){
            showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
            showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), true);
        }
        super.notifyDataSetChanged();
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay && imageView.getVisibility() == View.VISIBLE) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
