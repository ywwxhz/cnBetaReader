package com.ywwxhz.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.service.NewsCommentService;

/**
 * Created by ywwxhz on 2014/11/2.
 */
public class NewsCommentActivity extends ExtendBaseActivity {
    private int padding;
    private int margin;
    private NewsCommentService mService;

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.list_layout);
        mService = new NewsCommentService(this);
        padding = mService.getParentView().getPaddingLeft();
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mService.getFloatButtom().getLayoutParams();
        margin = layoutParams.leftMargin;
        layoutParams.setMargins(margin, margin,
                margin + tintManager.getConfig().getPixelInsetRight(), margin + tintManager.getConfig().getPixelInsetBottom());
        mService.getFloatButtom().setLayoutParams(layoutParams);
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

    @Override
    protected void onConfigurationChangedNew(Configuration newConfig) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mService.getFloatButtom().getLayoutParams();
        layoutParams.setMargins(margin, margin,
                margin + tintManager.getConfig().getPixelInsetRight(), margin + tintManager.getConfig().getPixelInsetBottom());
        mService.getFloatButtom().setLayoutParams(layoutParams);
    }
}
