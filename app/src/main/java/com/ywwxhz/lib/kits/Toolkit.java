package com.ywwxhz.lib.kits;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ywwxhz.cnbetareader.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class Toolkit {
    private static Gson gson;
    private static Handler mUIHandler;

    public static  void runInUIThread(Runnable runnable,long delay){
        if(mUIHandler==null) {
            mUIHandler = new Handler(Looper.getMainLooper());
        }
        mUIHandler.postDelayed(runnable,delay);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[3];
    }

    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void showCrouton(Activity activity,String message,Style style){
        Crouton.makeText(activity,message,style, R.id.content).show();
    }

    public static void showCrouton(Activity activity,int messageRes,Style style){
        Crouton.makeText(activity,messageRes,style, R.id.content).show();
    }

    public static void setStatusBarDarkIcon(Activity activity, boolean on) {
        Window window = activity.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (on) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
                Class<? extends Window> clazz = window.getClass();
                try {
                    int darkModeFlag = 0;
                    Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                    Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                    darkModeFlag = field.getInt(layoutParams);
                    Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                    extraFlagField.invoke(window, on ? darkModeFlag : 0, darkModeFlag);
                } catch (Exception ignored) {
                }
            }

        }
    }
}
