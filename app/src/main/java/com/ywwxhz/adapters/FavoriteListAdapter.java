package com.ywwxhz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

import java.util.List;
import java.util.regex.Matcher;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/27 10:23.
 */
public class FavoriteListAdapter extends BaseAdapter<NewsItem> {
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    public FavoriteListAdapter(Context context, List<NewsItem> items) {
        super(context, items);
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        View view;
        ViewHoder hoder;
        if(convertView==null){
            view = infater.inflate(R.layout.favorite_list_item,parent,false);
            hoder = new ViewHoder(view);
            view.setTag(hoder);
        }else{
            view = convertView;
            hoder = (ViewHoder) view.getTag();
        }
        hoder.blindData(getDataSetItem(position));
        return view;
    }

    private class ViewHoder{
        private ImageView mImage;
        private TextView mTitle;

        public ViewHoder(View view) {
            mImage = (ImageView) view.findViewById(R.id.image);
            mTitle = (TextView) view.findViewById(R.id.title);
        }

        void blindData(NewsItem item){
            Matcher matcher = Configure.FAVOR_NEWS_TITLE.matcher(item.getTitle());
            String latter;
            if(matcher.find()){
                latter = matcher.group(3);
            }else{
                latter = String.valueOf(item.getTitle().charAt(0));
            }
            mTitle.setText(item.getTitle());
            mImage.setImageDrawable(
                    mDrawableBuilder.build(latter, mColorGenerator.getColor(item.getSid()))
            );
        }

    }
}
