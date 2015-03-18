package com.ywwxhz.widget.TranslucentStatus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Observable;
import java.util.Observer;


/**
 * com.ywwxhz.common.app
 * Created by 远望の无限 on 2014/12/13 23:17.
 */
public class TranslucentStatusHelper implements Translucentable, Observer {
    private SystemBarTintManager tintManager;
    private Option option;

    private TranslucentStatusHelper(Builder builder) {
        this.option = builder.option;
        this.option.addObserver(this);
        this.tintManager = buildTranslucent(builder.mActivity, builder.getOption()
                , builder.getTranslucentProxy(), builder.actionBarSizeAttr, builder.getStatusView());
    }

    public static Builder from(Activity activity) {
        return new Builder(activity);
    }

    /**
     * 通知配置更改
     */
    public void notifyConfigureChanged() {
        fixTranslucent(tintManager, option);
    }

    /**
     * 设置状态栏颜色
     *
     * @param statusColor Drawable
     */
    public void setStatusColor(Drawable statusColor) {
        option.setStatusColor(statusColor);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 颜色的16进制表示
     */
    public void setStatusColor(int color) {
        option.setStatusColor(color);
    }

    /**
     * 获取需要插入的像素
     *
     * @param withActionBar 是否包含Actionbar
     * @return
     */
    public int[] getInsertPixs(boolean withActionBar) {
        if (tintManager == null) {
            return new int[]{0, 0, 0, 0};
        }
        int paddings[] = new int[4];
        paddings[0] = 0;
        paddings[1] = tintManager.getConfig().getPixelInsetTop(withActionBar);
        paddings[2] = tintManager.getConfig().getPixelInsetRight();
        paddings[3] = tintManager.getConfig().getPixelInsetBottom();
        return paddings;
    }

    @Override
    public SystemBarTintManager getSystemBarTintManager() {
        return tintManager;
    }

    /**
     * 获取是否启用状态栏变色
     *
     * @return
     */
    public boolean isEnable() {
        return tintManager != null && tintManager.isStatusBarTintEnabled();
    }


    /**
     * 设置是否开启状态栏变色
     *
     * @param enable
     */
    public void setEnable(boolean enable) {
        if (tintManager != null) {
            tintManager.setStatusBarTintEnabled(enable);
        }
    }

    /**
     * 构造 SystemBarTintManager 要求Api level > 19 (Android 4.4) 否则返回 null
     *
     * @param activity
     * @param option            参数
     * @param proxy             Translucent策略
     * @param actionBarSizeAttr actionBarSize属性，用于兼容support v7 的 ActionBarActivity
     * @return 构造完成的 SystemBarTintManager 对象
     */
    public static SystemBarTintManager buildTranslucent(Activity activity, Option option
            , TranslucentProxy proxy, int actionBarSizeAttr) {
        return buildTranslucent(activity, option, proxy, actionBarSizeAttr, null);
    }

    /**
     * 构造 SystemBarTintManager 要求Api level > 19 (Android 4.4) 否则返回 null
     *
     * @param activity
     * @param option            参数
     * @param proxy             Translucent策略
     * @param actionBarSizeAttr actionBarSize属性，用于兼容support v7 的 ActionBarActivity
     * @param statusView        状态栏的View 当statusView对象为空时，statusView 插入Window.DirectView
     * @return 构造完成的 SystemBarTintManager 对象
     */
    public static SystemBarTintManager buildTranslucent(Activity activity, Option option
            , TranslucentProxy proxy, int actionBarSizeAttr, View statusView) {
        SystemBarTintManager tintManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, proxy);
            tintManager = new SystemBarTintManager(activity, actionBarSizeAttr, statusView);
            tintManager.setStatusBarTintEnabled(true);
            fixTranslucent(tintManager, option);
        }
        return tintManager;
    }

    public static void fixTranslucent(Translucentable translucentable, Option option) {
        fixTranslucent(translucentable.getSystemBarTintManager(), option);
    }

    public static void fixTranslucent(SystemBarTintManager tintManager, Option option) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && tintManager != null) {
            tintManager.setStatusBarTintDrawable(option.getStatusColor());
            tintManager.notifyConfigureChange();
            if (option.getConfigView() != null) {
                View configView = option.getConfigView();
                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
                if (!option.usingPadding && configView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) configView.getLayoutParams();
                    params.rightMargin = config.getPixelInsetRight();
                    switch (option.getInsertProxy()) {
                        case BOTH:
                            params.topMargin = config.getPixelInsetTop(option.isWithActionBar());
                            params.bottomMargin = config.getPixelInsetBottom();
                            break;
                        case TOP:
                            params.topMargin = config.getPixelInsetTop(option.isWithActionBar());
                            params.bottomMargin = 0;
                            break;
                        case BOTTOM:
                            params.topMargin = 0;
                            params.bottomMargin = config.getPixelInsetBottom();
                            break;
                        default:
                            params.topMargin = 0;
                            params.bottomMargin = 0;
                            break;
                    }
                    configView.setLayoutParams(params);
                } else {
                    int[] paddings = option.getPaddings();
                    if (configView instanceof ViewGroup) {
                        ((ViewGroup) configView).setClipToPadding(false);
                    }
                    int top = paddings[1], bottom = paddings[3];
                    switch (option.getInsertProxy()) {
                        case BOTH:
                            top += config.getPixelInsetTop(option.isWithActionBar());
                            bottom += config.getPixelInsetBottom();
                            break;
                        case TOP:
                            top += config.getPixelInsetTop(option.isWithActionBar());
                            break;
                        case BOTTOM:
                            bottom += config.getPixelInsetBottom();
                            break;
                        default:
                            break;
                    }
                    configView.setPadding(paddings[0], top, paddings[2] + config.getPixelInsetRight(), bottom);
                }
            }
        }
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option.deleteObserver(this);
        this.option = option;
        this.option.addObserver(this);
        notifyConfigureChanged();
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, TranslucentProxy proxy) {
        switch (proxy) {
            case NEVG_BAR:
                // 透明导航栏
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                break;
            case STATUS_BAR:
                // 透明状态栏
                activity.getWindow()
                        .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                break;
            case BOTH:
                // 透明状态栏
                activity.getWindow()
                        .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 透明导航栏
                activity.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                break;

        }
    }

    @Override
    public void update(Observable observable, Object data) {
        notifyConfigureChanged();
    }

    /**
     * TranslucentStatusHelper 的参数
     * 通过此类可以设置状态栏颜色，插入策略，使用Margin或是paddings，插入的view
     */
    public static class Option extends Observable {
        private boolean withActionBar = true;
        private View configView;
        private Drawable statusColor;
        private InsertProxy insertProxy;
        private boolean usingPadding = true;
        private int[] paddings;

        /**
         * 是否包含ActionBar
         *
         * @param withActionBar
         * @return
         */
        public Option setWithActionBar(boolean withActionBar) {
            this.withActionBar = withActionBar;
            return this;
        }

        /**
         * 设置需要修正的view
         *
         * @param configView
         * @return
         */
        public Option setConfigView(View configView) {
            if (configView != null && !configView.equals(this.configView)) {
                this.configView = configView;
                paddings = new int[]{configView.getPaddingLeft(),
                        configView.getPaddingTop(),
                        configView.getPaddingRight(),
                        configView.getPaddingBottom()
                };
                setChanged();
                notifyObservers();
            } else if (configView == null) {
                this.configView = null;
                paddings = new int[]{0, 0, 0, 0};
                setChanged();
                notifyObservers();
            }
            return this;
        }

        /**
         * 设置颜色
         *
         * @param statusColor
         * @return
         */
        public Option setStatusColor(Drawable statusColor) {
            this.statusColor = statusColor;
            setChanged();
            notifyObservers();
            return this;
        }

        /**
         * 设置颜色
         *
         * @param statusColor
         * @return
         */
        public Option setStatusColor(int statusColor) {
            this.statusColor = new ColorDrawable(statusColor);
            setChanged();
            notifyObservers();
            return this;
        }

        boolean isWithActionBar() {
            return withActionBar;
        }

        public View getConfigView() {
            return configView;
        }

        Drawable getStatusColor() {
            if (statusColor == null) {
                statusColor = new ColorDrawable(Color.TRANSPARENT);
            }
            return statusColor;
        }

        int[] getPaddings() {
            if (paddings == null) {
                paddings = new int[]{0, 0, 0, 0};
            }
            return paddings;
        }

        InsertProxy getInsertProxy() {
            if (insertProxy == null) {
                insertProxy = InsertProxy.NONE;
            }
            return insertProxy;
        }

        /**
         * 设置插入策略
         *
         * @param insertProxy
         * @return
         */
        public Option setInsertProxy(InsertProxy insertProxy) {
            if (!insertProxy.equals(this.insertProxy)) {
                this.insertProxy = insertProxy;
                setChanged();
            }
            notifyObservers();
            return this;
        }

        /**
         * 设置是否使用padding
         *
         * @param usingPadding true 使用padding
         *                     false 使用margin 前提是父view的layoutparams继承自MarginLayoutParams否则设置无效
         * @return
         */
        public Option setUsingPadding(boolean usingPadding) {
            this.usingPadding = usingPadding;
            return this;
        }
    }

    public enum InsertProxy {
        BOTH,//顶部和尾部
        TOP,//顶部
        BOTTOM,//底部
        NONE//不处理
    }

    public enum TranslucentProxy {
        STATUS_BAR,//透明状态栏
        NEVG_BAR,//透明导航栏
        BOTH//都透明
    }

    /**
     * 用于构造TranslucentStatusHelper
     */
    public static class Builder {
        private Activity mActivity;
        private Option option;
        private int actionBarSizeAttr = android.R.attr.actionBarSize;
        private TranslucentProxy translucentProxy;
        private View statusView;

        private Builder(Activity activity) {
            this.mActivity = activity;
        }

        public TranslucentStatusHelper builder() {
            return new TranslucentStatusHelper(this);
        }

        public Builder setActionBarSizeAttr(int actionBarSizeAttr) {
            this.actionBarSizeAttr = actionBarSizeAttr;
            return this;
        }

        int getActionBarSizeAttr() {
            return actionBarSizeAttr;
        }

        Option getOption() {
            if (option == null) {
                option = new Option();
            }
            return option;
        }

        public Builder setOption(Option option) {
            this.option = option;
            return this;
        }

        public Builder setTranslucentProxy(TranslucentProxy translucentProxy) {
            this.translucentProxy = translucentProxy;
            return this;
        }

        public TranslucentProxy getTranslucentProxy() {
            if (translucentProxy == null) {
                translucentProxy = TranslucentProxy.STATUS_BAR;
            }
            return translucentProxy;
        }

        View getStatusView() {
            return statusView;
        }

        public Builder setStatusView(View statusView) {
            this.statusView = statusView;
            return this;
        }
    }
}
