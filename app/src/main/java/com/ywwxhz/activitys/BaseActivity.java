package com.ywwxhz.activitys;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.widget.TranslucentStatus.TranslucentStatusHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by ywwxhz on 2014/10/22.
 */
public abstract class BaseActivity extends Activity {

    protected TranslucentStatusHelper helper;
    protected TranslucentStatusHelper.Option option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        option = new TranslucentStatusHelper.Option()
                .setStatusColor(getResources().getColor(R.color.statusColor))
                .setInsertProxy(TranslucentStatusHelper.InsertProxy.TOP)
                .setUsingPadding(false);
        helper = TranslucentStatusHelper.from(this).setOption(option).builder();
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
