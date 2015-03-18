package com.ywwxhz.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * CnbetaReader
 * com.ywwxhz.app.fragment
 * Created by 远望の无限(ywwxhz) on 2015/3/12 22:14.
 */
public class FontSizeFragment extends DialogFragment {
    private static final String PROGRESS_KEY = "key_progress";
    private int progress;
    private DiscreteSeekBar.OnProgressChangeListener listener;

    public static FontSizeFragment getInstance(int progress){
        FontSizeFragment fragment = new FontSizeFragment();
        Bundle args = new Bundle(1);
        args.putInt(PROGRESS_KEY,progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null&&args.containsKey(PROGRESS_KEY)){
            progress = args.getInt(PROGRESS_KEY);
        }else{
            progress = 100;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DiscreteSeekBar mSeekBar = new DiscreteSeekBar(getActivity());
        mSeekBar.setIndicatorFormatter("%d%%");
        mSeekBar.setMax(150);
        mSeekBar.setMin(50);
        mSeekBar.setProgress(progress);
        mSeekBar.setOnProgressChangeListener(listener);
        return mSeekBar;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public void setSeekBarListener(DiscreteSeekBar.OnProgressChangeListener listener){
        this.listener = listener;
    }

}
