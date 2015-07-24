package com.ywwxhz.fragments;

import android.view.View;
import android.widget.TextView;

import com.ywwxhz.adapters.FavoriteListAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.impl.FavoriteNewsListDataProvider;
import com.ywwxhz.processers.NewsListProcesser;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/26 14:27.
 */
public class FavoriteNewsListFragment extends BaseNewsListFragment<FavoriteListAdapter,FavoriteNewsListDataProvider> {
    @Override
    public FavoriteNewsListDataProvider getProvider() {
        return new FavoriteNewsListDataProvider(getActivity());
    }

    @Override
    protected NewsListProcesser<FavoriteNewsListDataProvider> createProcesser(FavoriteNewsListDataProvider provider) {
        return new NewsListProcesser<FavoriteNewsListDataProvider>(provider){
            private TextView message;
            @Override
            public void assumeView(View view) {
                super.assumeView(view);
                message = (TextView) view.findViewById(R.id.message);
                message.setText(R.string.message_no_favorite);
            }
            @Override
            public void onLoadFinish(int size) {
                super.onLoadFinish(size);
                if (getProvider().getAdapter().getCount() > 0) {
                    message.setVisibility(View.GONE);
                } else {
                    message.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}
