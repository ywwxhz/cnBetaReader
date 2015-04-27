package com.ywwxhz.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.ThemeManger;
import com.ywwxhz.lib.kits.UIKit;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2015/4/7 8:54.
 */
public class ChoiseThemeFragment extends DialogFragment implements View.OnClickListener {
    private int which;
    private android.widget.RadioGroup themegroup;
    private android.widget.Button ok;

    public ChoiseThemeFragment setCallBack(ChoiseThemeFragment.callBack callBack) {
        this.callBack = callBack;
        return this;
    }

    private callBack callBack;

    public interface callBack{
        void onSelect(int witch);
    }
    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if (width > UIKit.dip2px(getActivity(), 320)) {
            width = UIKit.dip2px(getActivity(), 320);
        }
        getDialog().getWindow().setLayout(width, getDialog().getWindow().getAttributes().height);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        this.ok = (Button) view.findViewById(R.id.ok);
        this.ok.setOnClickListener(this);
        this.themegroup = (RadioGroup) view.findViewById(R.id.theme_group);
        String[] themes = getActivity().getResources().getStringArray(R.array.theme_text);
        int currentTheme = ThemeManger.getCurrentTheme(getActivity());
        int padding10 = UIKit.dip2px(getActivity(), 10);
        int padding5 = UIKit.dip2px(getActivity(), 5);
        for (int i=0;i<themes.length;i++) {
            RadioButton button = new RadioButton(getActivity());
            button.setText(themes[i]);
            button.setId(i);
            button.setPadding(padding10, padding5,padding10, padding5);
            button.setCompoundDrawablePadding(padding10);
            button.setTextSize(16);
            themegroup.addView(button);
        }
        themegroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                which = checkedId;
            }
        });
        themegroup.check(currentTheme);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ok){
            if(callBack!=null){
                callBack.onSelect(which);
            }
        }
    }
}
