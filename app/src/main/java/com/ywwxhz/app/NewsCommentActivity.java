package com.ywwxhz.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.processer.NewsCommentProcesser;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    private NewsCommentProcesser mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        mService = new NewsCommentProcesser(this);
        option.setConfigView(mService.getParentView());
        fixPos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixPos();
    }
    private void fixPos() {
        int[] ints = helper.getInsertPixs(false);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mService.getFloatButtom().getLayoutParams();
        int margin = layoutParams.leftMargin;
        layoutParams.rightMargin = margin + ints[2];
        layoutParams.bottomMargin = margin + ints[3];
        mService.getFloatButtom().setLayoutParams(layoutParams);
    }
}
