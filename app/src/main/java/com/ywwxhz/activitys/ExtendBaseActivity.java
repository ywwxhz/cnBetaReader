package com.ywwxhz.activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/3 17:51.
 */
public abstract class ExtendBaseActivity extends BaseToolBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }
}
