package com.ywwxhz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.hoderview.TopicItemView;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 20:49.
 */
public class TopicListAdapter extends BaseAdapter<TopicItem> {

    public interface onClickCallBack{
        void onClick(TopicListAdapter adapter,TopicItem item);
    }

    public void setCallBack(onClickCallBack callBack) {
        this.callBack = callBack;
    }

    private onClickCallBack callBack;
    private TextDrawable.IBuilder mDrawableBuilder;
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private View.OnClickListener listener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TopicItem item  = (TopicItem) view.getTag();
            if(callBack!=null){
                callBack.onClick(TopicListAdapter.this,item);
            }
        }
    };
    public TopicListAdapter(Context context, List<TopicItem> items) {
        super(context, items);
        mDrawableBuilder = TextDrawable.builder().beginConfig()
                .withBorder(4).endConfig().round();
    }

    @Override
    protected View bindViewAndData(LayoutInflater infater, int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = infater.inflate(R.layout.susbcribe_list_item,parent,false);
        }
        ((TopicItemView)convertView).showTopic(getDataSetItem(position),mDrawableBuilder,mColorGenerator,listener);
        return convertView;
    }

}
