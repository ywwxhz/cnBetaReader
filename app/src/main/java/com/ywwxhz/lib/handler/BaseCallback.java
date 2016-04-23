package com.ywwxhz.lib.handler;

import android.support.annotation.Nullable;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2016/4/23 21:48.
 */
public abstract class BaseCallback<T> extends AbsCallback<T> {


    @Override
    public final T parseNetworkResponse(final Response response) {
        try {
            return parseResponse(response);
        } catch (final Exception e) {
            OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                @Override
                public void run() {
                    onError(response.code(), response, e);
                }
            });
        }
        return null;
    }

    protected abstract T parseResponse(Response response) throws Exception;


    @Override
    public final void onResponse(boolean isFromCache, T t, Request request, @Nullable Response response) {
        onResponse(t);
    }

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程
     */
    public final void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        if (response == null) {
            onError(0, null, e);
        } else {
            onError(response.code(), response, e);
        }
    }

    protected abstract void onError(int httpCode, Response response, Exception cause);

    protected abstract void onResponse(T t);
}
