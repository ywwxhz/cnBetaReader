package com.ywwxhz.app;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.ywwxhz.lib.SystemBarTintManager;
import com.ywwxhz.lib.kits.UIKit;

/**
 * Created by ywwxhz on 2014/10/22.
 */
public abstract class BaseActivity extends Activity implements UIKit.Translucentable {
    protected SystemBarTintManager tintManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView(savedInstanceState);
        tintManager = UIKit.buildTranslucentStatus(this, getInsertView(), shouldFixPose(), getStatusDrawable());
        onViewCreated(savedInstanceState);
    }

    protected abstract void createView(Bundle savedInstanceState);

    protected void onViewCreated(Bundle savedInstanceState) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UIKit.fixTranslucentStatusPadding(tintManager, getInsertView(), getPaddingMode(), getPadding());
        onConfigurationChangedNew(newConfig);
    }

    protected abstract View getInsertView();

    protected boolean shouldFixPose() {
        return false;
    }

    protected Drawable getStatusDrawable() {
        return new ColorDrawable(0x88000000);
    }

    protected UIKit.PaddingMode getPaddingMode() {
        return UIKit.PaddingMode.NONE;
    }

    protected int[] getPadding() {
        return null;
    }

    protected void onConfigurationChangedNew(Configuration newConfig) {
    }

    @Override
    public SystemBarTintManager getSystemBarTintManager() {
        return tintManager;
    }
}
