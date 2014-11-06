package com.ywwxhz.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.service.TopicCommentService;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class TopicCommentActivity extends ExtendBaseActivity {
    //todo:
    private int padding;
    private TopicCommentService mService;

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.list_layout);
        mService = new TopicCommentService(this);
        padding = mService.getParentView().getPaddingLeft();
    }

    @Override
    protected int[] getPadding() {
        return new int[]{padding};
    }

    @Override
    protected boolean shouldFixPose() {
        return true;
    }

    @Override
    protected View getInsertView() {
        return mService.getParentView();
    }

    @Override
    protected UIKit.PaddingMode getPaddingMode() {
        return UIKit.PaddingMode.ALL_SAME;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
