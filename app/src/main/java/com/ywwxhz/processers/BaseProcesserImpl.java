package com.ywwxhz.processers;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.data.BaseDataProvider;
import com.ywwxhz.data.DataProviderCallback;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/2 20:10.
 */
public abstract class BaseProcesserImpl<E,DataProvider extends BaseDataProvider<E>> implements BaseProcesser<E,DataProvider>, DataProviderCallback<E> {

    protected ActionBarActivity mActivity;
    protected DataProvider provider;
    protected int colorPrimary;
    protected int colorPrimaryDark;
    protected int titleColor;
    protected int windowBackground;

    public BaseProcesserImpl(DataProvider provider) {
        this.provider = provider;
        provider.setCallback(this);
    }

    @Override
    public void setActivity(ActionBarActivity activity) {
        this.mActivity = activity;
        this.provider.setActivity(activity);
        TypedArray array = activity.obtainStyledAttributes(new int[]{R.attr.colorPrimary,
                R.attr.colorPrimaryDark,R.attr.titleColor,android.R.attr.windowBackground});
        colorPrimary = array.getColor(0,activity.getResources().getColor(R.color.toolbarColor));
        colorPrimaryDark = array.getColor(1,activity.getResources().getColor(R.color.statusColor));
        titleColor = array.getColor(2,activity.getResources().getColor(R.color.toolbarColor));
        windowBackground = array.getColor(3, Color.WHITE);
        array.recycle();
    }

    @Override
    public ActionBarActivity getActivity() {
        return mActivity;
    }

    @Override
    public void setProvider(DataProvider provider) {
        this.provider = provider;
        provider.setCallback(this);
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void loadData(boolean startup) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }
}
