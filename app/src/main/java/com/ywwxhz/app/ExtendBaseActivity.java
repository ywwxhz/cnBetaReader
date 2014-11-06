package com.ywwxhz.app;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.ywwxhz.cnbetareader.R;

/**
 * Created by ywwxhz on 2014/11/3.
 */
public abstract class ExtendBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    protected Drawable getStatusDrawable() {
        return new ColorDrawable(getResources().getColor(R.color.statusColor));
    }
}
