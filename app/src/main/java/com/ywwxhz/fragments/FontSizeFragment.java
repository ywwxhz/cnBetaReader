package com.ywwxhz.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.kits.PrefKit;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * CnbetaReader
 * com.ywwxhz.app.fragment
 * Created by 远望の无限(ywwxhz) on 2015/3/12 22:14.
 */
public class FontSizeFragment extends DialogFragment implements View.OnClickListener {
    private static final String PROGRESS_KEY = "key_progress";
    private int progress;
    private DiscreteSeekBar.OnProgressChangeListener listener;
    private DiscreteSeekBar mSeekBar;

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
        View view = inflater.inflate(R.layout.fragment_font_size, container, false);
        view.findViewById(R.id.buttom_permanent_save).setOnClickListener(this);
        view.findViewById(R.id.buttom_restore_default).setOnClickListener(this);
        view.findViewById(R.id.buttom_temporary_save).setOnClickListener(this);
        mSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seek_font_size);
        mSeekBar.setOnProgressChangeListener(listener);
        mSeekBar.setProgress(progress);
        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttom_permanent_save:
                PrefKit.writeInt(getActivity(), "font_size", mSeekBar.getProgress());
                break;
            case R.id.buttom_temporary_save:
                break;
            case R.id.buttom_restore_default:
                listener.onProgressChanged(mSeekBar,100,true);
                mSeekBar.setProgress(100);
                PrefKit.delete(getActivity(),"font_size");
                break;
        }
        this.dismiss();
    }
}
