package com.ywwxhz.adapter;

import android.content.Context;
import android.text.Html;
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

    public NewsListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
    }

    public NewsListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        ViewHoder hoder;
        View view;
        if (convertView == null || convertView.getTag() == null) {
            view = infater.inflate(R.layout.news_list_item, parent, false);
            hoder = new ViewHoder(view);
            view.setTag(hoder);
        } else {
            view = convertView;
            hoder = (ViewHoder) convertView.getTag();
        }
        NewsItem item = getDataSetItem(position);
        hoder.news_title.setText(item.getTitle());
        hoder.news_publisher.setText(item.getAid());
        hoder.news_time.setText(item.getInputtime());
        String counter;
        if (item.getCounter() > 9999) {
            counter = "9999+";
        } else {
            counter = item.getCounter() + "";
        }
        if (PrefKit.getBoolean(view.getContext(),view.getContext().getString(R.string.pref_show_list_news_image_key), true) && item.getThumb().contains("thumb")) {
            hoder.news_image_hoder.setVisibility(View.VISIBLE);
            MyApplication.getPicasso().load(item.getThumb().replaceAll("(\\.\\w{3,4})?_100x100|thumb/mini/", ""))
                    .resize(800,320).centerCrop().placeholder(R.drawable.imagehoder).error(R.drawable.imagehoder_error)
                    .into(hoder.news_image);
        } else {
            hoder.news_image_hoder.setVisibility(View.GONE);
        }
        hoder.news_views.setText(counter);
        if (item.getHometext() != null) {
            hoder.news_summary.setText(Html.fromHtml(item.getHometext()));
        }
        return view;
    }

    class ViewHoder {
        TextView news_time;
        TextView news_title;
        TextView news_views;
        TextView news_summary;
        TextView news_publisher;
        ImageView news_image;
        View news_image_hoder;

        public ViewHoder(View view) {
            this.news_time = (TextView) view.findViewById(R.id.news_time);
            this.news_title = (TextView) view.findViewById(R.id.news_title);
            this.news_views = (TextView) view.findViewById(R.id.news_views);
            this.news_summary = (TextView) view.findViewById(R.id.news_summary);
            this.news_publisher = (TextView) view.findViewById(R.id.news_publisher);
            this.news_image = (ImageView) view.findViewById(R.id.news_image);
            this.news_image_hoder = view.findViewById(R.id.news_image_hoder);
        }
    }
}
