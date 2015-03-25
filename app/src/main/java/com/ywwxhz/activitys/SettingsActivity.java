package com.ywwxhz.activitys;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.UIKit;

import java.io.File;

/**
 * cnBetaReader
 *
 * Created by 远望の无限(ywwxhz) on 2014/11/3 17:52.
 */
public class SettingsActivity extends ExtendBaseActivity {

    private boolean haschange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            haschange = false;
            getFragmentManager().beginTransaction().replace(R.id.content, new GeneralPreference()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
        this.finish();
    }

    private void setResult(){
        if (haschange) {
            setResult(200);
        }
    }

    public static class GeneralPreference extends PreferenceFragment {
        private ListView mListView;
        private Preference preference;
        private Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (getActivity() instanceof SettingsActivity) {
                    ((SettingsActivity) getActivity()).haschange = true;
                }
                return false;
            }
        };

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
            findPreference(getString(R.string.pref_show_large_image_key)).setOnPreferenceClickListener(listener);
            findPreference(getString(R.string.pref_show_list_news_image_key)).setOnPreferenceClickListener(listener);
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

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mListView = UIKit.getHideListView(this);
            if (getActivity() instanceof SettingsActivity) {
                ((SettingsActivity) getActivity()).option.setConfigView(mListView);
            }
        }

        private String getVersionName() {
            return "Ver. " + BuildConfig.VERSION_NAME;
        }

        @Override
        public void onDetach() {
            super.onDetach();
            if (getActivity() instanceof SettingsActivity) {
                ((SettingsActivity) getActivity()).option.setConfigView(null);
            }
        }
    }
}
