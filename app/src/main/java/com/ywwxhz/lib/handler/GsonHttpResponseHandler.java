package com.ywwxhz.lib.handler;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.ywwxhz.lib.kits.Toolkit;

import org.apache.http.Header;

import java.lang.reflect.Type;

/**
 * Created by ywwxhz on 2014/9/23.
 */
public abstract class GsonHttpResponseHandler<T> extends TextHttpResponseHandler {

    protected Type type;

    public GsonHttpResponseHandler(TypeToken<T> typeToken) {
        this.type = typeToken.getType();
    }

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (statusCode == 200) {
            try {
                T e = Toolkit.getGson().fromJson(responseString, type);
                if (e != null) {
                    onSuccess(statusCode, headers, responseString, e);
                } else {
                    onFailure(statusCode, headers, responseString, new RuntimeException("response empty"));
                }
            } catch (Exception e) {
                onError(statusCode, headers, responseString, e);
            }
        }
    }

    protected abstract void onError(int statusCode, Header[] headers, String responseString, Throwable cause);

    public abstract void onSuccess(int statusCode, Header[] headers, String responseString, T object);

    @Override
    public void onProgress(int bytesWritten, int totalSize) {
    }
}
