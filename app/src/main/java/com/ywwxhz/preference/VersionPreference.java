package com.ywwxhz.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/8/3 21:06.
 */
public class VersionPreference extends Preference {
    public VersionPreference(Context context) {
        super(context);
    }

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VersionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        setTitle(R.string.pref_version_title);
        setSummary(getVersionName());
    }
    private String getVersionName() {
        return "Ver. " + BuildConfig.VERSION_NAME + " Build " +BuildConfig.buildDate+ " " + BuildConfig.BUILD_TYPE;
    }
}
