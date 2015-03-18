package com.ywwxhz.lib.kits;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceFragment;
import android.widget.ListView;

import java.lang.reflect.Method;

/**
 * Created by ywwxhz on 2014/10/7.
 */
public class UIKit {

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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
