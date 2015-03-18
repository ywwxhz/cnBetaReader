package com.ywwxhz.app;

import android.app.ActionBar;
import android.os.Bundle;

/**
 * Created by ywwxhz on 2014/11/3.
 */
public abstract class ExtendBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }
}
