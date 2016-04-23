package com.ywwxhz.hoderview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.widget.textdrawable.TextDrawable;
import com.ywwxhz.widget.textdrawable.util.ColorGenerator;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 20:50.
 */
public class TopicItemView extends RelativeLayout {
    private ImageView mImage;
    private TextView mTitle;
    private Button mAction;

    public TopicItemView(Context context) {
        super(context);
        init(context);
    }

    public TopicItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopicItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = (ImageView) findViewById(R.id.image);
        mTitle = (TextView) findViewById(R.id.title);
        mAction = (Button) findViewById(R.id.action);
    }


    public void showTopic(TopicItem item, TextDrawable.IBuilder mDrawableBuilder, ColorGenerator mColorGenerator,OnClickListener listener) {
        mAction.setTag(item);
        mTitle.setText(item.getTopicName());
        if(item.isSaved()){
            mAction.setText("取消关注");
        }else{
            mAction.setText("关注");
        }
        mAction.setOnClickListener(listener);
        mImage.setImageDrawable(mDrawableBuilder.build(String.valueOf(item.getTopicName().charAt(0))
                ,mColorGenerator.getColor(item.getTopicId())));
    }
}
