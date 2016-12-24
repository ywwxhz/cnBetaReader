package com.ywwxhz.activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.PrefKit;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * 扩展 BaseToolBarActivity 支持滑动返回
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/3 17:51.
 */
public abstract class ExtendBaseActivity extends BaseToolBarActivity implements SwipeBackActivityBase {

    private SwipeBackActivityHelper mSwipeBackActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mSwipeBackActivityHelper = new SwipeBackActivityHelper(this);
        mSwipeBackActivityHelper.onActivityCreate();
        setSwipeBackEnable(PrefKit.getBoolean(this, R.string.pref_swipeback_key, true));
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackActivityHelper.getSwipeBackLayout();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackActivityHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mSwipeBackActivityHelper != null)
            return mSwipeBackActivityHelper.findViewById(id);
        return v;
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
