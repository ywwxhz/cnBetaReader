package com.ywwxhz.lib.handler;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.ywwxhz.MyApplication;
import com.ywwxhz.cnbetareader.R;
import com.ywwxhz.entitys.ResponseObject;
import com.ywwxhz.lib.kits.Toolkit;

import de.keyboardsurfer.android.widget.crouton.Style;
import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2014/11/2 18:01.
 */
public abstract class BaseResponseObjectResponse<T> extends BaseGsonCallback<ResponseObject<T>> {

    protected BaseResponseObjectResponse(TypeToken<ResponseObject<T>> type) {
        super(type);
    }

    @Override
    protected void onError(int httpCode, Response response, Exception cause) {
        if (httpCode == 200) {
            Toolkit.showCrouton(getActivity(), R.string.message_data_structure_change, Style.ALERT);
        } else {
            Toolkit.showCrouton(getActivity(), R.string.message_no_network, Style.ALERT);
        }
        if (cause != null) {
            cause.printStackTrace();
            if (MyApplication.getInstance().getDebug()) {
                Toast.makeText(getActivity(), cause.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected final void onResponse(ResponseObject<T> object) {
        if (object != null) {
            if ("success".equals(object.getState())) {
                onSuccess(object.getResult());
            } else {
                onError(200, null, new RuntimeException("empty ResponseObject"));
            }
        }
    }

    /**
     * 成功调用
     * @param result
     */
    protected abstract void onSuccess(T result);

    /**
     * 获取Activity
     * @return  {@link Activity}
     */
    protected abstract Activity getActivity();
}
