package com.ywwxhz.widget.TranslucentStatus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ywwxhz.cnbetareader.R;


/**
 * com.ywwxhz.common.app
 * Created by 远望の无限 on 2014/12/13 23:17.
 */
public class TranslucentStatusHelper {

    private Activity mActivity;
    private View mStatusBarView;
    private ColorDrawable statusDrawable;
    private static final boolean mTranslucentAvalible;
    private boolean mTranslucentEnable = false;
    private int actionBarSizeAttr = android.R.attr.actionBarSize;
    private TranslucentProxy translucentProxy = TranslucentProxy.AUTO;

    static {
        mTranslucentAvalible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private TranslucentStatusHelper(Activity activity) {
        this.mActivity = activity;
    }

    public static Builder from(Activity activity) {
        return new Builder(activity);
    }

    private void init() {
        if (mTranslucentAvalible) {
            initTranslucentStatus();
            if (mTranslucentEnable) {
                initStatusBar();
            }
        }
    }


    /**
     * 设置状态栏颜色
     *
     * @param statusDrawable Drawable
     */
    public void setStatusDrawable(ColorDrawable statusDrawable) {
        if (isEnable()) {
            setBackgroundCompat(mStatusBarView, statusDrawable);
        }
    }

    public ColorDrawable getStatusDrawable() {
        return statusDrawable;
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 颜色的16进制表示
     */
    public void setStatusColor(int color) {
        if (isEnable()) {
            setStatusDrawable(new ColorDrawable(color));
        }
    }

    /**
     * 获取是否启用状态栏变色
     *
     * @return
     */
    public boolean isEnable() {
        return mTranslucentAvalible && mTranslucentEnable;
    }


    /**
     * 设置是否开启状态栏变色
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        if (mTranslucentAvalible) {
            mTranslucentEnable = enable;
            mStatusBarView.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    public int getPixelInsetTop(boolean withActionBar) {
        return isEnable() ? getStatusBarHeight() + (withActionBar ? getActionBarHeight() : 0) : 0;
    }

    @TargetApi(19)
    private void initTranslucentStatus() {
        switch (translucentProxy) {
            case KITKAT:
                // 透明状态栏
                mActivity.getWindow()
                        .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                mTranslucentEnable = true;
                break;
            case AUTO:
                Window win = mActivity.getWindow();
                // check theme attrs
                TypedArray a = mActivity.obtainStyledAttributes(new int[]{android.R.attr.windowTranslucentStatus});
                try {
                    if (a.getBoolean(0, false)) {
                        mActivity.getWindow()
                                .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        mTranslucentEnable = true;
                        return;
                    }
                } finally {
                    a.recycle();
                }

                // check window flags
                if ((win.getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0) {
                    mTranslucentEnable = true;
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ViewGroup view = (ViewGroup) mActivity.findViewById(android.R.id.content);
                    for (int i=0;i<view.getChildCount();i++){
                        if(view.getChildAt(i) instanceof DrawerLayout){
                            if(view.getChildAt(i).getFitsSystemWindows()){
                                mTranslucentEnable = false;
                                return;
                            }
                        }
                    }
                    mTranslucentEnable = true;
                    win.setStatusBarColor(Color.TRANSPARENT);
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
                break;
            case NONE:
                mTranslucentEnable = false;
                break;

        }
    }

    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        mActivity.getTheme().resolveAttribute(actionBarSizeAttr, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, mActivity.getResources().getDisplayMetrics());
    }

    private int getStatusBarHeight() {
        Resources res = mActivity.getResources();
        int result = 0;
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initStatusBar() {
        if (mStatusBarView == null) {
            ViewGroup decorViewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
            mStatusBarView = new View(mActivity);
            FrameLayout.LayoutParams mStatusBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                    , getStatusBarHeight());
            mStatusBarParams.gravity = Gravity.TOP;
            mStatusBarView.setLayoutParams(mStatusBarParams);
            decorViewGroup.addView(mStatusBarView);
        } else {
            ViewGroup.LayoutParams mStatusBarParams = mStatusBarView.getLayoutParams();
            if (mStatusBarParams == null) {
                throw new IllegalStateException("view must attach to parent view");
            }
            mStatusBarParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mStatusBarParams.height = getStatusBarHeight();
            mStatusBarView.setLayoutParams(mStatusBarParams);
        }
        setBackgroundCompat(mStatusBarView, statusDrawable);
    }

    private void setBackgroundCompat(View v, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(drawable);
        } else {
            v.setBackgroundDrawable(drawable);
        }
    }

    public static void TranslucentStatusBar(Activity activity) {
        // 透明状态栏
        activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public enum TranslucentProxy {
        KITKAT,
        AUTO,
        NONE
    }

    /**
     * 用于构造TranslucentStatusHelper
     */
    public static class Builder {
        private TranslucentStatusHelper helper;

        private Builder(Activity activity) {
            helper = new TranslucentStatusHelper(activity);
        }

        public TranslucentStatusHelper builder() {
            if(helper.statusDrawable==null){
                TypedArray array = helper.mActivity.obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
                setStatusColor(array.getColor(0, 0x9F000000));
                array.recycle();
            }
            helper.init();
            return helper;
        }

        public Builder setActionBarSizeAttr(int actionBarSizeAttr) {
            helper.actionBarSizeAttr = actionBarSizeAttr;
            return this;
        }

        public Builder setStatusColorDrawable(ColorDrawable drawable) {
            helper.statusDrawable = drawable;
            return this;
        }

        public Builder setStatusColor(int color) {
            helper.statusDrawable = new ColorDrawable(color);
            return this;
        }

        public Builder setTranslucentProxy(TranslucentProxy translucentProxy) {
            helper.translucentProxy = translucentProxy;
            return this;
        }

        public Builder setStatusView(View statusView) {
            helper.mStatusBarView = statusView;
            return this;
        }
    }

}
