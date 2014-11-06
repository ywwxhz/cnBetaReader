package com.ywwxhz.lib.kits;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.ywwxhz.lib.SystemBarTintManager;

import java.lang.reflect.Method;

/**
 * Created by ywwxhz on 2014/10/7.
 */
public class UIKit {

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, int color) {
        return buildTranslucentStatus(activity, new ColorDrawable(color));
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, Drawable drawable) {
        return buildTranslucentStatus(activity, activity.findViewById(android.R.id.content), true, drawable);
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, boolean fixpos, int color) {
        return buildTranslucentStatus(activity, null, fixpos, new ColorDrawable(color));
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, boolean fixpos, Drawable drawable) {
        return buildTranslucentStatus(activity, null, fixpos, drawable);
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, View contaner, int color) {
        return buildTranslucentStatus(activity, contaner, true, new ColorDrawable(color));
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, View contaner, Drawable drawable) {
        return buildTranslucentStatus(activity, contaner, true, drawable);
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, View contaner, boolean fixpos, int color) {
        return buildTranslucentStatus(activity, contaner, fixpos, new ColorDrawable(color));
    }

    public static SystemBarTintManager buildTranslucentStatus(Activity activity, View contaner, boolean fixpos, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintDrawable(drawable);
        if (fixpos) {
            if (contaner != null) {
                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
                if (contaner instanceof ViewGroup) {
                    ((ViewGroup) contaner).setClipToPadding(false);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    contaner.setPadding(contaner.getPaddingLeft(),
                            contaner.getPaddingTop() + config.getPixelInsetTop(true),
                            contaner.getPaddingRight() + config.getPixelInsetRight(),
                            contaner.getPaddingBottom() + config.getPixelInsetBottom());
                }
            }
        }
        return tintManager;
    }

    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        // 透明状态栏
        activity.getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明导航栏
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    public static void fixTranslucentStatusPadding(Activity activity, View contaner, PaddingMode mode, int... paddings) {
        try {
            fixTranslucentStatusPadding(((Translucentable) activity).getSystemBarTintManager(), contaner, mode, paddings);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Activity must implements Translucentable");
        }
    }

    public static void fixTranslucentStatusPadding(Translucentable translucentable, View contaner, PaddingMode mode, int... paddings) {
        fixTranslucentStatusPadding(translucentable.getSystemBarTintManager(), contaner, mode, paddings);
    }

    public static void fixTranslucentStatusPadding(SystemBarTintManager tintManager, View contaner, PaddingMode mode, int... paddings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (contaner != null) {
                int left, top, right, bottom;
                left = top = right = bottom = 0;
                try {
                    switch (mode) {
                        case LEFT_RIGHT:
                            left = paddings[0];
                            right = paddings[1];
                            break;
                        case LEFT_RIGHT_SAME:
                            left = right = paddings[0];
                            break;
                        case SET_ALL:
                            left = paddings[0];
                            top = paddings[1];
                            right = paddings[2];
                            bottom = paddings[3];
                            break;
                        case ALL_SAME:
                            left = top = right = bottom = paddings[0];
                            break;
                        case NONE:
                            left = top = right = bottom = 0;
                            break;

                    }
                } catch (IndexOutOfBoundsException ex) {
                    throw new IllegalArgumentException("PaddingMode must much paddings");
                }
                tintManager.notifyConfigureChange();
                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
                if (contaner instanceof ViewGroup) {
                    ((ViewGroup) contaner).setClipToPadding(false);
                }
                contaner.setPadding(left, top + config.getPixelInsetTop(true)
                        , right + config.getPixelInsetRight(), bottom + config.getPixelInsetBottom());
            }
        }
    }

    public static ListView getHideListView(PreferenceFragment fragment) {
        ListView listView = null;
        try {
            Method getListView = fragment.getClass().getMethod("getListView");
            listView = (ListView) getListView.invoke(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listView;
    }

    public static void setBackIcon(ActionBar actionbar, Drawable backIcon) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod(
                    "setBackButtonDrawable", new Class[]{Drawable.class});
            method.invoke(actionbar, backIcon);
        } catch (Exception ignored) {
        }
    }

    public enum PaddingMode {
        LEFT_RIGHT, LEFT_RIGHT_SAME, SET_ALL, ALL_SAME, NONE
    }

    public interface Translucentable {
        public SystemBarTintManager getSystemBarTintManager();
    }
}
