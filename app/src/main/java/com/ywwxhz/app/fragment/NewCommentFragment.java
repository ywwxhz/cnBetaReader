package com.ywwxhz.app.fragment;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.kits.NetKit;
import com.ywwxhz.lib.kits.UIKit;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
    private EditText seccode;
    private View send;
    private ProgressWheel progress;
    private ImageView seccodeImage;
    private boolean flushing = false;

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
        content = (EditText) view.findViewById(R.id.push_content);
        seccode = (EditText) view.findViewById(R.id.seccode);
        seccodeImage = (ImageView) view.findViewById(R.id.seccodeImage);
        progress = (ProgressWheel) view.findViewById(R.id.seccodeProgress);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    send.setEnabled(false);
                } else {
                    send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        send = view.findViewById(R.id.send);
        send.setOnClickListener(this);
        reflushscecode();
        seccodeImage.setOnClickListener(reflushscecode);
        progress.setOnClickListener(reflushscecode);
    }

    private View.OnClickListener reflushscecode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reflushscecode();
        }
    };

    private void reflushscecode() {
        if (!flushing) {
            flushing = true;
            seccodeImage.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            RequestParams params = new RequestParams();
            params.put("refresh", 1);
            params.put("_", System.currentTimeMillis());
            NetKit.getInstance().getClient().get(getActivity(), Configure.SECOND_VIEW, NetKit.getAuthHeader(), params,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            progress.spin();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String url = response.getString("url");
                                NetKit.getInstance().getClient().get(getActivity(), Configure.BASE_URL + url, NetKit.getAuthHeader()
                                        , null, new BinaryHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                                        //工厂对象的decodeByteArray把字节转换成Bitmap对象
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                                        //设置图片
                                        seccodeImage.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                                        seccodeImage.setImageBitmap(null);
                                        error.printStackTrace();
                                        showToast("获取验证码失败");
                                    }

                                    @Override
                                    public void onFinish() {
                                        flushing = false;
                                        seccodeImage.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onProgress(int bytesWritten, int totalSize) {
                                        progress.setProgress(bytesWritten * 1.0f / totalSize);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast("获取验证码失败了");
                                flushing = false;
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            showToast("获取验证码失败了");
                            flushing = false;
                            seccodeImage.setImageBitmap(null);
                            seccodeImage.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    }
            );
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if (width > UIKit.dip2px(getActivity(), 450)) {
            width = UIKit.dip2px(getActivity(), 450);
        }
        getDialog().getWindow().setLayout(width, getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            if (seccode.getText().length() == 4) {
                RequestParams params = new RequestParams();
                params.put("op", "publish");
                params.put("content", content.getText().toString());
                params.put("sid", sid);
                params.put("pid", tid);
                params.put("seccode", seccode.getText().toString());
                params.put("csrf_token", token);
                NetKit.getInstance().getClient().post(getActivity(), Configure.COMMENT_VIEW,
                        NetKit.getAuthHeader(), params, NetKit.CONTENT_TYPE,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                System.out.println("statusCode = [" + statusCode + "], response = [" + response + "]");
                                try {
                                    if ("success".equals(response.getString("state"))) {
                                        String action;
                                        if ("0".equals(tid)) {
                                            action = "发布";
                                        } else {
                                            action = "回复";
                                        }
                                        showToast(action + "成功");
                                        dismiss();
                                    } else if ("error".equals(response.getString("state"))) {
                                        showToast(response.getString("error") + "");
                                    } else {
                                        throw new JSONException("response error");
                                    }
                                } catch (JSONException e) {
                                    onFailure(statusCode, headers, e, response);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                throwable.printStackTrace();
                                String action;
                                if ("0".equals(tid)) {
                                    action = "发布";
                                } else {
                                    action = "回复";
                                }
                                showToast(action + "失败");
                            }
                        }
                );
            } else {
                showToast("验证码不能为空");
            }

        }
    }

    private void showToast(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        if (width > UIKit.dip2px(getActivity(), 420)) {
            width = UIKit.dip2px(getActivity(), 420);
        }
        getDialog().getWindow().setLayout(width, getDialog().getWindow().getAttributes().height);
    }
}
