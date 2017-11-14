package com.ywwxhz.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.CroutonStyle;
import com.ywwxhz.lib.kits.FileKit;
import com.ywwxhz.lib.kits.PrefKit;
import com.ywwxhz.lib.kits.Toolkit;

import java.io.File;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/3/25 18:08.
 */
public class SettingFragment extends PreferenceFragment {

	Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			MyApplication.getInstance().setListImageShowStatusChange(true);
			return true;
		}
	};
	private Preference preference;
	private boolean running = false;
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
							FileKit.deleteDir(
									new File(MyApplication.getInstance().getInternalCacheDir().getAbsolutePath()
											+ "/../app_webview"));
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
			} else if (preference.getKey().equals(getString(R.string.pref_block_list_key))) {
				View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_block_list, null);
				final EditText editText = (EditText) view.findViewById(R.id.editText);
				editText.setText(PrefKit.getString(getContext(), R.string.pref_block_list_key, "[广告]\nitiger.com"));
				final AlertDialog dialog = new AlertDialog.Builder(getContext())
						.setTitle(R.string.pref_block_list_title).setView(view)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {
								String text = editText.getText().toString();
								PrefKit.writeString(getContext(), R.string.pref_block_list_key, text);
								MyApplication.getInstance().updateBlockList();
							}
						}).create();
				dialog.show();
			} else {
				throw new RuntimeException("Test Application Crash");
			}
			return false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		preference = findPreference(getString(R.string.pref_clean_cache_key));
		preference.setSummary(getFileSize());
		preference.setOnPreferenceClickListener(onPreferenceClickListener);
		findPreference(getString(R.string.pref_crash_key)).setOnPreferenceClickListener(onPreferenceClickListener);
		// findPreference(getString(R.string.pref_check_update_key)).setOnPreferenceClickListener(onPreferenceClickListener);
		findPreference(getString(R.string.pref_show_list_news_image_key))
				.setOnPreferenceChangeListener(onPreferenceChangeListener);
		findPreference(getString(R.string.pref_block_list_key)).setOnPreferenceClickListener(onPreferenceClickListener);
	}

	private String getFileSize() {
		long size = 0;
		size += FileKit.getFolderSize(MyApplication.getInstance().getInternalCacheDir());
		size += FileKit.getFolderSize(MyApplication.getInstance().getExternalCacheDir());
		size += FileKit.getFolderSize(
				new File(MyApplication.getInstance().getInternalCacheDir().getAbsolutePath() + "/../app_webview"));
		return Formatter.formatFileSize(getActivity(), size);
	}
}
