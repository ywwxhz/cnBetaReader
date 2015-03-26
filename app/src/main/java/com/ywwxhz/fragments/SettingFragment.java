package com.ywwxhz.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.text.format.Formatter;

import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.FileKit;

import java.io.File;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 18:08.
 */
public class SettingFragment extends PreferenceFragment {

    private Preference preference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        findPreference(getString(R.string.pref_version_key)).setSummary(getVersionName());
        preference = findPreference(getString(R.string.pref_clean_cache_key));
        preference.setSummary(getFileSize());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FileKit.deleteDir(getActivity().getCacheDir());
                try {
                    FileKit.deleteDir(getActivity().getExternalCacheDir());
                } catch (Exception ignored) {
                }
                FileKit.deleteDir(new File(getActivity().getCacheDir().getAbsolutePath() + "/../app_webview"));
                preference.setSummary(getFileSize());
                return false;
            }
        });
    }

    private String getFileSize() {
        long size = 0;
        size += FileKit.getFolderSize(getActivity().getCacheDir());
        try {
            size += FileKit.getFolderSize(getActivity().getExternalCacheDir());
        } catch (Exception ignored) {
        }
        size += FileKit.getFolderSize(new File(getActivity().getCacheDir().getAbsolutePath() + "/../app_webview"));
        return Formatter.formatFileSize(getActivity(), size);
    }

    private String getVersionName() {
        return "Ver. " + BuildConfig.VERSION_NAME;
    }
}
