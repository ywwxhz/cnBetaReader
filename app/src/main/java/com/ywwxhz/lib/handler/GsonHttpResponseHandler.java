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
                if(e!=null) {
                    onSuccess(statusCode, headers, responseString, e);
                }else{
                    onFailure(statusCode,headers,responseString,new RuntimeException("response empty"));
                }
            } catch (Exception e) {
                onError(statusCode, headers, responseString, e);
            }
        }
    }

    protected abstract void onError(int statusCode, Header[] headers, String responseString, Throwable cause);

    public abstract Type getType();

    public abstract void onSuccess(int statusCode, Header[] headers, String responseString, T object);

    @Override
    public void onProgress(int bytesWritten, int totalSize) {}
}
