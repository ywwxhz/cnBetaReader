package com.ywwxhz.fragments;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.lib.Configure;
import com.ywwxhz.lib.handler.BaseJsonCallback;
import com.ywwxhz.lib.kits.UIKit;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/5 17:54.
 */
public class AddNewCommentFragment extends DialogFragment implements View.OnClickListener {
	private static final String SID_KEY = "key_sid";
	private static final String TOKEN_KEY = "key_token";
	private static final String TID_KEY = "key_tid";
	private int sid;
	private String tid;
	private String token;
	private EditText content;
	private EditText seccode;
	private Button send;
	private ProgressWheel progress;
	private ImageView seccodeImage;
	private boolean flushing = false;
	private View.OnClickListener reflushscecode = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			reflushscecode();
		}
	};

	public static AddNewCommentFragment getInstance(int sid, String tid, String token) {
		AddNewCommentFragment fragment = new AddNewCommentFragment();
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
		send = (Button) view.findViewById(R.id.send);
		send.setOnClickListener(this);
		if ("0".equals(tid)) {
			send.setText("发布");
		} else {
			send.setText("回复");
		}
		reflushscecode();
		seccodeImage.setOnClickListener(reflushscecode);
		progress.setOnClickListener(reflushscecode);
	}

	private void reflushscecode() {
		if (!flushing) {
			flushing = true;
			seccodeImage.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			HttpParams params = new HttpParams();
			params.put("refresh", "1");
			params.put("csrf_token", token);
			params.put("_", System.currentTimeMillis());
			OkGo.get(Configure.SECOND_VIEW).params(params).tag(getActivity()).execute(new BaseJsonCallback() {

				@Override
				public void onBefore(BaseRequest request) {
					progress.spin();
				}

				@Override
				protected void onError(int httpCode, Response response, Exception cause) {
					showToast("获取验证码失败了");
					flushing = false;
					seccodeImage.setImageBitmap(null);
					seccodeImage.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
				}

				@Override
				protected void onResponse(JSONObject response) {
					try {
						String url = response.getString("url");
						OkGo.get(Configure.BASE_URL + url).tag(getActivity()).execute(new BitmapCallback() {

							@Override
							public void onSuccess(Bitmap bitmap, Call call, Response response) {
								seccodeImage.setImageBitmap(bitmap);
							}

							@Override
							public void onError(Call call, Response response, Exception e) {
								seccodeImage.setImageBitmap(null);
								showToast("获取验证码失败");
							}

							@Override
							public void onAfter(@Nullable Bitmap bitmap, @Nullable Exception e) {
								flushing = false;
								seccodeImage.setVisibility(View.VISIBLE);
								progress.setVisibility(View.GONE);
							}

						});
					} catch (Exception e) {
						e.printStackTrace();
						showToast("获取验证码失败了");
						flushing = false;
					}
				}
			});
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
				HttpParams params = new HttpParams();
				params.put("op", "publish");
				params.put("content", content.getText().toString());
				params.put("sid", sid + "");
				params.put("pid", tid);
				params.put("seccode", seccode.getText().toString());
				params.put("csrf_token", token);
				//
				OkGo.post(Configure.COMMENT_VIEW).headers("Accept", "application/json, text/javascript, */*; q=0.01")
						.headers("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").params(params).tag(getActivity())
						.execute(new BaseJsonCallback() {
							@Override
							protected void onError(int httpCode, Response response, Exception cause) {
								String action;
								if ("0".equals(tid)) {
									action = "发布";
								} else {
									action = "回复";
								}
								showToast(action + "失败");
								if (cause != null) {
									showToast(cause.getLocalizedMessage());
									cause.printStackTrace();
								}
							}

							@Override
							protected void onResponse(JSONObject response) {
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
										showToast(response.getString("error"));
									} else {
										throw new JSONException(response.toString());
									}
								} catch (JSONException e) {
									onError(200, null, e);
								}
							}
						});
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
