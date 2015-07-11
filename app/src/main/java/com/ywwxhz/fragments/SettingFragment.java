package com.ywwxhz.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.text.format.Formatter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywwxhz.MyApplication;
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
                            FileKit.deleteDir(MyApplication.getInstance().getInternalCacheDir());
                            try {
                                FileKit.deleteDir(MyApplication.getInstance().getExternalCacheDir());
                            } catch (Exception ignored) {
                            }
                            FileKit.deleteDir(new File(MyApplication.getInstance().getInternalCacheDir().getAbsolutePath() + "/../app_webview"));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Toolkit.showCrouton(getActivity(), "缓存清理完成", CroutonStyle.INFO);
                            try {
                                preference.setSummary(getFileSize());
                            }catch (Exception ignored){

                            }
                            running = false;
                        }
                    }.execute();
                }
                return false;
            }
        });
        Preference theme = findPreference("theme");
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
                        ImageLoader.getInstance().clearMemoryCache();
                        ThemeManger.changeToTheme(getActivity(), which);
                    }
                }).show(getActivity().getFragmentManager(), "theme");
                return false;
            }
        });
        findPreference(getString(R.string.pref_show_large_image_key)).setOnPreferenceChangeListener(listener);
        findPreference(getString(R.string.pref_show_list_news_image_key)).setOnPreferenceChangeListener(listener);
    }

    Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            MyApplication.getInstance().setListImageShowStatusChange(true);
            return true;
        }
    };

    private String getFileSize() {
        long size = 0;
        size += FileKit.getFolderSize(MyApplication.getInstance().getInternalCacheDir());
        size += FileKit.getFolderSize(MyApplication.getInstance().getExternalCacheDir());
        size += FileKit.getFolderSize(new File(MyApplication.getInstance().getInternalCacheDir().getAbsolutePath() + "/../app_webview"));
        return Formatter.formatFileSize(getActivity(), size);
    }

    private String getVersionName() {
        return "Ver. " + BuildConfig.VERSION_NAME +" " +BuildConfig.BUILD_TYPE;
    }
}
