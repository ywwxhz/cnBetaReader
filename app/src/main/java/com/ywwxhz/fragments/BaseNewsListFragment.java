package com.ywwxhz.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.ListDataProvider;
import com.ywwxhz.processers.NewsListProcesserImpl;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 18:06.
 */
public abstract class BaseNewsListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(hasOptionMenu());
    }

    private NewsListProcesserImpl processer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        processer = new NewsListProcesserImpl(getActivity(),view,getProvider());
        processer.loadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        processer.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        processer.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return processer.onOptionsItemSelected(item)|| super.onOptionsItemSelected(item);
    }

    public abstract ListDataProvider getProvider();

    public boolean hasOptionMenu(){
        return false;
    }

    @Override
    public void onDestroy() {
        processer.onDestroy();
        super.onDestroy();
    }
}
