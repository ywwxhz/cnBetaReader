package com.ywwxhz.lib.kits;

import android.app.Activity;

import com.google.gson.Gson;
import com.ywwxhz.cnbetareader.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by ywwxhz on 2014/11/1.
 */
public class Toolkit {
    private static Gson gson;

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
}
