package com.ywwxhz.lib.handler;

import com.loopj.android.http.TextHttpResponseHandler;
import com.ywwxhz.lib.kits.Toolkit;

import org.apache.http.Header;

import java.lang.reflect.Type;

/**
 * Created by ywwxhz on 2014/9/23.
 */
public abstract class GsonHttpResponseHandler<T> extends TextHttpResponseHandler {

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (statusCode == 200) {
            try {
                T e = Toolkit.getGson().fromJson(responseString, getType());
                onSuccess(statusCode, headers, responseString, e);
            } catch (Exception e) {
                onError(statusCode, headers, responseString, e);
            }
        }
    }

    protected abstract void onError(int statusCode, Header[] headers, String responseString, Throwable cause);

    public abstract Type getType();

    public abstract void onSuccess(int statusCode, Header[] headers, String responseString, T object);

}
