package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.TopicComment;

import java.util.List;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class TopicCommentAdapter extends BaseAdapter<TopicComment> {

    public TopicCommentAdapter(Context context, List<TopicComment> items) {
        super(context, items);
    }

    public TopicCommentAdapter(Context context) {
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
        return view;
    }

    class ViewHoder {
        TextView news_time;
        TextView news_title;
        TextView news_views;
        TextView news_summary;
        TextView news_publisher;

        public ViewHoder(View view) {
            this.news_time = (TextView) view.findViewById(R.id.news_time);
            this.news_title = (TextView) view.findViewById(R.id.news_title);
            this.news_views = (TextView) view.findViewById(R.id.news_views);
            this.news_summary = (TextView) view.findViewById(R.id.news_summary);
            this.news_publisher = (TextView) view.findViewById(R.id.news_publisher);
        }
    }
}
