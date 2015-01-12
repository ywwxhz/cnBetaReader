package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ywwxhz.app.MyApplication;
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
    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        showLarge = PrefKit.getBoolean(context, context.getString(R.string.pref_show_large_image_key), false);
        showImage = PrefKit.getBoolean(context, context.getString(R.string.pref_show_list_news_image_key), false);
        if(showLarge) {
            layout =  R.layout.news_list_item;
        }else{
            layout = R.layout.news_list_item1;
        }
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        ViewHoder hoder;
        View view;
        if (convertView == null || convertView.getTag() == null) {
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
            if(hoder.news_image_hoder.getVisibility() == View.GONE) {
                hoder.news_image_hoder.setVisibility(View.VISIBLE);
            }
            if(showLarge){
                if (item.getLargeImage()!=null) {
                    MyApplication.getPicasso().load(item.getLargeImage())
                            .fit().centerCrop()
                            .placeholder(R.drawable.imagehoder).error(R.drawable.imagehoder_error)
                            .into(hoder.news_image);
                }else{
                    hoder.news_image_hoder.setVisibility(View.GONE);
                }
            }else{
                MyApplication.getPicasso().load(item.getThumb())
                        .placeholder(R.drawable.imagehoder_sm).noFade().error(R.drawable.imagehoder_error_sm)
                        .into(hoder.news_image);
            }
        } else {
            if(hoder.news_image_hoder.getVisibility() == View.VISIBLE) {
                hoder.news_image_hoder.setVisibility(View.GONE);
            }
        }
        hoder.news_summary.setText(item.getHometext());
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
