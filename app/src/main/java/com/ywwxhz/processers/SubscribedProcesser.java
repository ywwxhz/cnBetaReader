package com.ywwxhz.processers;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ywwxhz.MyApplication;
import com.ywwxhz.adapters.TopicListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.TopicScribedDataProvider;
import com.ywwxhz.entitys.TopicItem;
import com.ywwxhz.lib.database.exception.DbException;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/3 19:44.
 */
public class SubscribedProcesser extends BaseListProcesser<TopicItem,TopicScribedDataProvider> {
    private TextView message;

    public SubscribedProcesser(TopicScribedDataProvider provider) {
        super(provider);
        provider.getAdapter().setCallBack(new TopicListAdapter.onClickCallBack() {
            @Override
            public void onClick(TopicListAdapter adapter, TopicItem item) {
                item.setSaved(!item.isSaved());
                adapter.notifyDataSetChanged();
                try {
                    MyApplication.getInstance().getDbUtils().saveOrUpdate(item);
                    Toast.makeText(getActivity(), "操作成功", Toast.LENGTH_SHORT).show();
                } catch (DbException e) {
                    Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        message = (TextView) view.findViewById(R.id.message);
        message.setText(R.string.message_no_subscribe);
    }

    @Override
    public void onLoadFinish() {
        super.onLoadFinish();
        if (getProvider().getAdapter().getCount() > 0) {
            message.setVisibility(View.GONE);
        } else {
            message.setVisibility(View.VISIBLE);
        }
    }
}
