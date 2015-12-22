package com.ywwxhz.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.text.format.Formatter;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.Toolkit;
import com.ywwxhz.update.UpdateHelper;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        preference = findPreference(getString(R.string.pref_clean_cache_key));
        preference.setSummary(getFileSize());
        preference.setOnPreferenceClickListener(onPreferenceClickListener);
        findPreference(getString(R.string.pref_crash_key)).setOnPreferenceClickListener(onPreferenceClickListener);
        findPreference(getString(R.string.pref_check_update_key)).setOnPreferenceClickListener(onPreferenceClickListener);
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
            if (preference.getKey().equals(getString(R.string.pref_clean_cache_key))) {
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
            }if (preference.getKey().equals(getString(R.string.pref_check_update_key))) {
                Toast.makeText(getActivity(), "正在检查更新中", Toast.LENGTH_SHORT).show();
                UpdateHelper.build(getActivity(), MyApplication.getInstance().getUpdateUrl(),
                        new UpdateHelper.Options()
                                .setShowIgnoreVersion(true)
                                .setHintVersion(true)
                ).check();
            } else {
                throw new RuntimeException("Test Application Crash");
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
}
