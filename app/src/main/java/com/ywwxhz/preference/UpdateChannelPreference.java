package com.ywwxhz.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import com.ywwxhz.cnbetareader.BuildConfig;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.PrefKit;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/8/3 21:25.
 */
public class UpdateChannelPreference extends Preference {
    public static final String[] Channel = {"release", "releasePreview"};
    private int channelid = 0;

    public UpdateChannelPreference(Context context) {
        super(context);
        init(context);
    }

    public UpdateChannelPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UpdateChannelPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setTitle(R.string.pref_release_channel_title);
        setSummary(PrefKit.getString(context, R.string.pref_release_channel_key, BuildConfig.BUILD_TYPE));
        if (Channel[0].equals(PrefKit.getString(context, R.string.pref_release_channel_key, BuildConfig.BUILD_TYPE))) {
            channelid = 0;
        }else{
            channelid = 1;
        }
    }

    @Override
    protected void onClick() {
        new AlertDialog.Builder(getContext()).setTitle(R.string.pref_release_channel_title)
                .setSingleChoiceItems(Channel, channelid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        channelid = which;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefKit.writeString(getContext(), getContext().getString(R.string.pref_release_channel_key), Channel[channelid]);
                        setSummary(Channel[channelid]);
                    }
                }).create().show();
    }
}
