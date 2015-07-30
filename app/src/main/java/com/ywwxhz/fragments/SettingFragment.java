package com.ywwxhz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;

import com.balysv.materialripple.MaterialRippleLayout;
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
    private Context context;
    private int themeid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        findPreference(getString(R.string.pref_version_key)).setSummary(getVersionName());
        preference = findPreference(getString(R.string.pref_clean_cache_key));
        preference.setSummary(getFileSize());
        preference.setOnPreferenceClickListener(onPreferenceClickListener);
        Preference theme = findPreference("theme");
        theme.setSummary(getResources().getStringArray(R.array.theme_text)[ThemeManger.getCurrentTheme(getActivity())]);
        theme.setOnPreferenceClickListener(onPreferenceClickListener);
        findPreference(getString(R.string.pref_show_large_image_key)).setOnPreferenceChangeListener(onPreferenceChangeListener);
        findPreference(getString(R.string.pref_show_list_news_image_key)).setOnPreferenceChangeListener(onPreferenceChangeListener);
        findPreference(getString(R.string.pref_enable_ripple_key)).setOnPreferenceChangeListener(onPreferenceChangeListener);
    }

    Preference.OnPreferenceChangeListener onPreferenceChangeListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(getString(R.string.pref_enable_ripple_key))) {
                MaterialRippleLayout.setEnableRipple((Boolean) newValue);
            } else {
                MyApplication.getInstance().setListImageShowStatusChange(true);
            }
            return true;
        }
    };

    Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equals(getString(R.string.pref_clean_cache_key))){
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
                            try {
                                SettingFragment.this.preference.setSummary(getFileSize());
                                Toolkit.showCrouton(getActivity(), "缓存清理完成", CroutonStyle.INFO);
                            } catch (Exception ignored) {

                            }
                            running = false;
                        }
                    }.execute();
                }
                return false;
            }else{
                new AlertDialog.Builder(getActivity()).setTitle(R.string.theme)
                        .setSingleChoiceItems(R.array.theme_text, ThemeManger.getCurrentTheme(getActivity()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                themeid = which;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).changeTheme = true;
                                }
                                ImageLoader.getInstance().clearMemoryCache();
                                ThemeManger.changeToTheme(getActivity(), themeid);
                            }
                        }).create().show();
            }
            return false;
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
        return "Ver. " + BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE;
    }
}
