package com.ywwxhz.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ywwxhz.cnbetareader.R;

import java.util.Locale;

/**
 * Created by ywwxhz on 2014/11/5.
 */
public class NewCommentFragment extends DialogFragment implements View.OnClickListener {
    private static final String SID_KEY = "key_sid";
    private static final String TOKEN_KEY = "key_token";
    private static final String TID_KEY = "key_tid";
    private int sid;
    private String tid;
    private String token;
    private EditText content;
    private TextView textCount;
    private View send;

    public static NewCommentFragment getInstance(int sid, String tid, String token) {
        NewCommentFragment fragment = new NewCommentFragment();
        Bundle args = new Bundle();
        args.putInt(SID_KEY, sid);
        args.putString(TID_KEY, tid);
        args.putString(TOKEN_KEY, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sid = getArguments().getInt(SID_KEY);
            tid = getArguments().getString(TID_KEY);
            token = getArguments().getString(TOKEN_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        textCount = (TextView) view.findViewById(R.id.text_count);
        content = (EditText) view.findViewById(R.id.push_content);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 320) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
                textCount.setText(String.format(Locale.CHINA, "%s / 320", s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        send = view.findViewById(R.id.send);
        send.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            //todo:
            String action;
            if ("0".equals(tid)) {
                action = "发布";
            } else {
                action = "回复";
            }
            Toast.makeText(getActivity(), action + "成功", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
