package com.ywwxhz.activitys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public abstract class BaseToolBarActivity extends ActionBarActivity {
    protected TranslucentStatusHelper.Option option;
    protected TranslucentStatusHelper helper;
    protected FrameLayout content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_basetoolbar);
        helper = TranslucentStatusHelper.from(this)
                .setStatusView(findViewById(R.id.statusView))
                .setActionBarSizeAttr(R.attr.actionBarSize)
                .setTranslucentProxy(TranslucentStatusHelper.TranslucentProxy.STATUS_BAR)
                .builder();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        content = (FrameLayout) findViewById(R.id.content);
        option = new TranslucentStatusHelper.Option()
                .setStatusColor(getResources().getColor(R.color.statusColor))
                .setInsertProxy(TranslucentStatusHelper.InsertProxy.NONE);
        helper.setOption(option);
    }

    @Override
    public void setContentView(int layoutResID) {
        content.removeAllViews();
        getLayoutInflater().inflate(layoutResID, content);
    }

    public void setContentViewSuper(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        content.removeAllViews();
        content.addView(view);
    }

    public void setContentViewUseSuper(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        content.removeAllViews();
        content.addView(view, params);
    }

    public void setContentViewsSuper(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        helper.notifyConfigureChanged();
    }

    @Override
    protected void onDestroy() {
        Crouton.clearCroutonsForActivity(this);
        super.onDestroy();
    }
}
