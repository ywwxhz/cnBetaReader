package com.ywwxhz.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.UIKit;
import com.ywwxhz.service.NewsListService;


public class NewsListActivity extends BaseActivity {

    private int padding;
    private int margin;
    private NewsListService mService;

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.list_layout);
        getActionBar().setDisplayShowHomeEnabled(true);
        mService = new NewsListService(this);
        padding = mService.getListView().getPaddingLeft();
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
    protected void onResume() {
        super.onResume();
        mService.onResume();
    }

    @Override
    protected boolean shouldFixPose() {
        return true;
    }

    @Override
    protected View getInsertView() {
        return mService.getListView();
    }

    @Override
    protected UIKit.PaddingMode getPaddingMode() {
        return UIKit.PaddingMode.ALL_SAME;
    }

    @Override
    protected int[] getPadding() {
        return new int[]{padding};
    }

    @Override
    protected Drawable getStatusDrawable() {
        return new ColorDrawable(getResources().getColor(R.color.statusColor));
    }

    @Override
    protected void onConfigurationChangedNew(Configuration newConfig) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mService.getFloatButtom().getLayoutParams();
        layoutParams.setMargins(margin, margin,
                margin + tintManager.getConfig().getPixelInsetRight(), margin + tintManager.getConfig().getPixelInsetBottom());
        mService.getFloatButtom().setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mService.onReturn(requestCode,resultCode);
    }
}
