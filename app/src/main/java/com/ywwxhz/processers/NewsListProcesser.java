package com.ywwxhz.processers;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ywwxhz.MyApplication;
import com.ywwxhz.adapters.BaseAdapter;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.data.NewsCacheHandler;
import com.ywwxhz.entitys.NewsItem;
import com.ywwxhz.lib.kits.FileCacheKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.util.List;

/**
 * cnBetaReader
 * Created by 远望の无限(ywwxhz) on 2014/11/1 17:46.
 */
public class NewsListProcesser<DataProvider extends ListDataProvider<NewsItem,? extends BaseAdapter<NewsItem>>> extends BaseListProcesser<NewsItem,DataProvider> {

    private NewsCacheHandler handler;

    public NewsListProcesser(DataProvider provider) {
        super(provider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_cache){
            if(handler==null) {
                handler = new NewsCacheHandler(getActivity());
            }
            handler.setCacheList(getProvider().getAdapter().getDataSet());
            handler.start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_news_list,menu);
    }

    @Override
    public void onDestroy() {
        if(handler!=null){
            handler.stop();
            handler.cleanNotification();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MyApplication.getInstance().isListImageShowStatusChange()) {
            provider.getAdapter().notifyDataSetChanged(true);
        }else{
            if(FileCacheKit.getInstance().isCached(provider.getTypeKey().hashCode()+"","list")) {
                provider.getAdapter().notifyDataSetChanged();
                List<NewsItem> dataSet = provider.getAdapter().getDataSet();
                if (dataSet.size() > 0) {
                    if (dataSet.size() > 40) {
                        dataSet = dataSet.subList(0, 39);
                    }
                    FileCacheKit.getInstance().putAsync(provider.getTypeKey().hashCode() + "", Toolkit.getGson().toJson(dataSet), "list", null);
                }
            }
        }
    }
}
