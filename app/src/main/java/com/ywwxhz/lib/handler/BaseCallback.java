package com.ywwxhz.lib.handler;

import com.lzy.okgo.callback.AbsCallback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * cnBetaReader
 * <p/>
 * Created by 远望の无限(ywwxhz) on 2016/4/23 21:48.
 */
public abstract class BaseCallback<T> extends AbsCallback<T> {

    @Override
    public void onSuccess(T t, Call call, Response response) {
        onResponse(t);
    }

    @Override
    public final void onError(Call call, Response response, Exception e) {
        if (response == null) {
            onError(0, null, e);
        } else {
            onError(response.code(), response, e);
        }
    }

    /**
     * 失败响应回调
     * 用于兼容旧版接口
     *
     * @param httpCode http状态码
     * @param response 响应对象
     * @param cause    错误原因
     */
    protected abstract void onError(int httpCode, Response response, Exception cause);

    /**
     * 调用成功回调<br/>
     * 用于兼容旧版接口，如使用该方法请不要覆写 onSuccess(T t, Call call, Response response)
     *
     * @param t
     */
    protected abstract void onResponse(T t);
}
