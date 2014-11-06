package com.ywwxhz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entity.CommentItem;

import java.util.List;
import java.util.Locale;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class CommentListAdapter extends BaseAdapter<CommentItem> {
    public CommentListAdapter(Context context, List<CommentItem> items) {
        super(context, items);
    }

    public CommentListAdapter(Context context) {
        super(context);
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        ViewHoder hoder;
        View view;
        if (convertView == null || convertView.getTag() == null) {
            view = infater.inflate(R.layout.news_comment_item, parent, false);
            hoder = new ViewHoder(view);
            view.setTag(hoder);
        } else {
            view = convertView;
            hoder = (ViewHoder) convertView.getTag();
        }
        CommentItem item = getDataSetItem(position);
        hoder.comment_name.setText(String.format(Locale.CHINA, "%s [%s]", item.getName(), item.getHost_name()));
        if (item.getRefContent().length() != 0) {
            hoder.comment_ref.setVisibility(View.VISIBLE);
            hoder.comment_ref.setText(item.getRefContent());
        } else {
            hoder.comment_ref.setVisibility(View.GONE);
        }
        hoder.comment_content.setText(item.getComment());
        hoder.comment_time.setText(item.getDate());
        String score;
        if (item.getScore() > 999) {
            score = "999+";
        } else {
            score = item.getScore() + "";
        }
        hoder.comment_score.setText(score);
        String reason;
        if (item.getReason() > 999) {
            reason = "999+";
        } else {
            reason = item.getReason() + "";
        }
        hoder.comment_reason.setText(reason);
        return view;
    }

    class ViewHoder {
        TextView comment_name;
        TextView comment_ref;
        TextView comment_content;
        TextView comment_reason;
        TextView comment_score;
        TextView comment_time;

        public ViewHoder(View view) {
            this.comment_name = (TextView) view.findViewById(R.id.comment_name);
            this.comment_ref = (TextView) view.findViewById(R.id.comment_ref);
            this.comment_content = (TextView) view.findViewById(R.id.comment_content);
            this.comment_reason = (TextView) view.findViewById(R.id.comment_reason);
            this.comment_score = (TextView) view.findViewById(R.id.comment_score);
            this.comment_time = (TextView) view.findViewById(R.id.comment_time);
        }
    }
}
