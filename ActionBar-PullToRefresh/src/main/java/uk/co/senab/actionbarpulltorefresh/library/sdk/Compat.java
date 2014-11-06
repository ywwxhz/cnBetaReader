package uk.co.senab.actionbarpulltorefresh.library.sdk;

import android.os.Build;
import android.view.View;

public class Compat {

    public static void setAlpha(View view, float alpha) {
        CompatV11.setAlpha(view, alpha);
    }

    public static void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            CompatV16.postOnAnimation(view, runnable);
        } else {
            CompatV11.postOnAnimation(view, runnable);
        }
    }

}
