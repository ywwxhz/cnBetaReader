package com.ywwxhz.lib;

import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/6 22:58.
 */
public class CroutonStyle {
    public static Style INFO;
    public static Style CONFIRM;
    public static void buildStyleInfo(int color){
        INFO = new Style.Builder(Style.INFO).setBackgroundColorValue(color&0xD0FFFFFF).build();
    }
    public static void buildStyleConfirm(int color){
        CONFIRM = new Style.Builder(Style.CONFIRM).setBackgroundColorValue(color&0xD0FFFFFF).build();
    }
}
