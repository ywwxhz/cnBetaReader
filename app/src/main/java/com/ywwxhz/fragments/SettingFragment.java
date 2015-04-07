package com.ywwxhz.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.text.format.Formatter;

import com.ywwxhz.activitys.MainActivity;
import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.io.File;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 18:08.
 */
public class SettingFragment extends PreferenceFragment {

    private Preference preference;
    private Preference theme;
    private boolean running = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        findPreference(getString(R.string.pref_version_key)).setSummary(getVersionName());
        preference = findPreference(getString(R.string.pref_clean_cache_key));
        preference.setSummary(getFileSize());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                if (!running) {
                    running = true;
                    Toolkit.showCrouton(getActivity(), "正在清理缓存中。请稍候。。", CroutonStyle.CONFIRM);
                    new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            FileKit.deleteDir(getActivity().getCacheDir());
                            try {
                                FileKit.deleteDir(getActivity().getExternalCacheDir());
                            } catch (Exception ignored) {
                            }
                            FileKit.deleteDir(new File(getActivity().getCacheDir().getAbsolutePath() + "/../app_webview"));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Toolkit.showCrouton(getActivity(), "缓存清理完成", CroutonStyle.INFO);
                            preference.setSummary(getFileSize());
                            running = false;
                        }
                    }.execute();
                }
                return false;
            }
        });
        theme = findPreference("theme");
        theme.setSummary(getResources().getStringArray(R.array.theme_text)[ThemeManger.getCurrentTheme(getActivity())]);
        theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new ChoiseThemeFragment().setCallBack(new ChoiseThemeFragment.callBack() {
                    @Override
                    public void onSelect(int which) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).changeTheme = true;
                        }
                        ThemeManger.changeToTheme(getActivity(), which);
                    }
                }).show(getActivity().getFragmentManager(),"theme");
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
        return "Ver. " + BuildConfig.VERSION_NAME +" " +BuildConfig.BUILD_TYPE;
    }
}
