package com.ywwxhz.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.activitys.MainActivity;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.ThemeManger;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/8/3 20:23.
 */
public class ChangeThemePreference extends Preference {
    private int themeid;

    public ChangeThemePreference(Context context) {
        super(context);
        init(context);
    }

    public ChangeThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ChangeThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setTitle(R.string.theme);
        setSummary(context.getResources()
                .getStringArray(R.array.theme_text)[ThemeManger.getCurrentTheme(context)]);
    }

    @Override
    protected void onClick() {
        new AlertDialog.Builder(getContext()).setTitle(R.string.theme)
                .setSingleChoiceItems(R.array.theme_text, ThemeManger.getCurrentTheme(getContext()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        themeid = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getContext() instanceof MainActivity) {
                            ((MainActivity) getContext()).changeTheme = true;
                            ImageLoader.getInstance().clearMemoryCache();
                            ThemeManger.changeToTheme(((MainActivity) getContext()), themeid);
                        }
                    }
                }).create().show();
    }
}
